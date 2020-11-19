package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayerEntity player;
    @Shadow private Vec3d requestedTeleportPos;

    // Injecting is always better than overwriting.
    @Inject(at = @At("HEAD"), method = "onPlayerInteractBlock(Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;)V", cancellable = true)
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo cbi) {
        cbi.cancel();
        NetworkThreadUtils.forceMainThread(packet, player.networkHandler, player.getServerWorld());
        ServerWorld serverWorld = player.getServerWorld();
        Hand hand = packet.getHand();
        ItemStack itemStack = player.getStackInHand(hand);
        BlockHitResult blockHitResult = packet.getBlockHitResult();
        BlockPos blockPos = blockHitResult.getBlockPos();
        Direction direction = blockHitResult.getSide();
        player.updateLastActionTime();
        if (blockPos.getY() < server.getWorldHeight()) {
            if (requestedTeleportPos == null && player.squaredDistanceTo((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) < ReachCommand.getReach(player, true) && serverWorld.canPlayerModifyAt(player, blockPos)) {
                ActionResult actionResult = player.interactionManager.interactBlock(player, serverWorld, itemStack, hand, blockHitResult);
                if (direction == Direction.UP && !actionResult.isAccepted() && blockPos.getY() >= server.getWorldHeight() - 1 && mc_canPlace(player, itemStack)) {
                    Text text = (new TranslatableText("build.tooHigh", server.getWorldHeight())).formatted(Formatting.RED);
                    player.networkHandler.sendPacket(new GameMessageS2CPacket(text, MessageType.GAME_INFO, Util.NIL_UUID));
                } else if (actionResult.shouldSwingHand()) {
                    player.swingHand(hand, true);
                }
            }
        } else {
            Text text2 = (new TranslatableText("build.tooHigh", server.getWorldHeight())).formatted(Formatting.RED);
            player.networkHandler.sendPacket(new GameMessageS2CPacket(text2, MessageType.GAME_INFO, Util.NIL_UUID));
        }
        player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
        player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
    }

    // Copied from super method, but uses reach.
    // Injects rather than overwrites cuz the Fabric API injects into the same method. -_-
    @Inject(at = @At("HEAD"), method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V", cancellable = true)
    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo cbi) {
        cbi.cancel();
        NetworkThreadUtils.forceMainThread(packet, player.networkHandler, player.getServerWorld());
        ServerWorld serverWorld = player.getServerWorld();
        Entity entity = packet.getEntity(serverWorld);
        player.updateLastActionTime();
        player.setSneaking(packet.isPlayerSneaking());
        if (entity != null) {
            if (player.squaredDistanceTo(entity) < ReachCommand.getReach(player, true)) {
                Hand hand = packet.getHand();
                ItemStack itemStack = hand != null ? player.getStackInHand(hand).copy() : ItemStack.EMPTY;
                Optional<ActionResult> optional = Optional.empty();
                if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.INTERACT) {
                    optional = Optional.of(player.interact(entity, hand));
                } else if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.INTERACT_AT) {
                    World world = player.getEntityWorld();
                    Entity e = packet.getEntity(world);
                    if (e != null) { // Manually adding the callback from the api, you're welcome. :3
                        EntityHitResult hitResult = new EntityHitResult(e, packet.getHitPosition().add(e.getX(), e.getY(), e.getZ()));
                        ActionResult result = UseEntityCallback.EVENT.invoker().interact(player, world, packet.getHand(), e, hitResult);
                        if (result != ActionResult.PASS) return;
                    }
                    optional = Optional.of(entity.interactAt(player, packet.getHitPosition(), hand));
                } else if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
                    if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof PersistentProjectileEntity || entity == player) {
                        player.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.invalid_entity_attacked"));
                        LOGGER.warn("Player {} tried to attack an invalid entity", player.getName().getString());
                        return;
                    }
                    player.attack(entity);
                }
                if (optional.isPresent() && optional.get().isAccepted()) {
                    Criteria.PLAYER_INTERACTED_WITH_ENTITY.test(player, itemStack, entity);
                    if (optional.get().shouldSwingHand()) {
                        player.swingHand(hand, true);
                    }
                }
            }
        }
    }

    // Straight up copied from the super class since that's easier than trying to shadow it somehow.
    private static boolean mc_canPlace(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            Item item = stack.getItem();
            return (item instanceof BlockItem || item instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(item);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager; broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "onDisconnected(Lnet/minecraft/text/Text;)V")
    public void onDisconnected_broadcastChatMessage(PlayerManager playerManager, Text msg, MessageType type, UUID id, Text reason) {
        if (playerManager.getServer().getWorld(World.OVERWORLD).getGameRules().getBoolean(MoreCommands.doJoinMessageRule) && !player.getDataTracker().get(MoreCommands.VANISH)) playerManager.broadcastChatMessage(msg, type, id);
    }

}
