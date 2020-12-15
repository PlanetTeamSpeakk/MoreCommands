package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity; squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", ordinal = 0), method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V")
    public double onPlayerInteractEntity_squaredDistanceTo(ServerPlayerEntity player, Entity entity) {
        return player.squaredDistanceTo(entity) < ReachCommand.getReach(player, true) ? 0 : 36;
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

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting; strip(Ljava/lang/String;)Ljava/lang/String;"), method = "onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;)V")
    public String onSignUpdate_strip(String s) {
        ServerPlayNetworkHandler thiz = MoreCommands.cast(this);
        if (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doSignColoursRule) || thiz.player.getServer().getPlayerManager().isOperator(thiz.player.getGameProfile()))
            s = Command.translateFormats(s);
        else s = Formatting.strip(s);
        return s;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag; getString(I)Ljava/lang/String;"), method = "onBookUpdate(Lnet/minecraft/network/packet/c2s/play/BookUpdateC2SPacket;)V")
    public String onBookUpdate_getString(ListTag list, int index) {
        ServerPlayNetworkHandler thiz = MoreCommands.cast(this);
        String s = list.getString(index);
        if (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doBookColoursRule) || thiz.player.getServer().getPlayerManager().isOperator(thiz.player.getGameProfile()))
            s = Command.translateFormats(s);
        return s;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils; normalizeSpace(Ljava/lang/String;)Ljava/lang/String;", remap = false), method = "onGameMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V")
    public String onGameMessage_normalizeSpace(String str) {
        ServerPlayNetworkHandler thiz = MoreCommands.cast(this);
        String s = StringUtils.normalizeSpace(str);
        if (!str.startsWith("/") && (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doChatColoursRule) || thiz.player.getServer().getPlayerManager().isOperator(thiz.player.getGameProfile())))
            s = Command.translateFormats(s);
        return s;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String; charAt(I)C", remap = false), method = "onGameMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V")
    public char onGameMessage_charAt(String string, int index) {
        ServerPlayNetworkHandler thiz = MoreCommands.cast(this);
        char ch = string.charAt(index);
        if (!string.startsWith("/") && ch == '\u00A7' && (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doChatColoursRule) || thiz.player.getServer().getPlayerManager().isOperator(thiz.player.getGameProfile())))
            ch = '&';
        return ch;
    }

}
