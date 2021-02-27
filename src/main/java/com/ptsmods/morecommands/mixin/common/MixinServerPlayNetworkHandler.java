package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.PlayerConnectionCallback;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
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
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayerEntity player;
    @Shadow private Vec3d requestedTeleportPos;
    private boolean mc_initialised = false;

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo cbi) {
        if (!mc_initialised) {
            mc_initialised = true;
            PlayerConnectionCallback.JOIN.invoker().call(player);
        }
    }

    @ModifyConstant(method = "onPlayerInteractBlock(Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;)V", constant = @Constant(doubleValue = 64.0D))
    public double onPlayerInteractBlock_maxReach(double d) {
        return ReachCommand.getReach(player, true);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity; squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", ordinal = 0), method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V")
    public double onPlayerInteractEntity_squaredDistanceTo(ServerPlayerEntity player, Entity entity) {
        return player.squaredDistanceTo(entity) < ReachCommand.getReach(player, true) ? 0 : 36;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager; broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "onDisconnected(Lnet/minecraft/text/Text;)V")
    public void onDisconnected_broadcastChatMessage(PlayerManager playerManager, Text msg, MessageType type, UUID id) {
        if (Objects.requireNonNull(playerManager.getServer().getWorld(World.OVERWORLD)).getGameRules().getBoolean(MoreCommands.doJoinMessageRule) && !player.getDataTracker().get(MoreCommands.VANISH)) playerManager.broadcastChatMessage(msg, type, id);
    }

    @Inject(at = @At("TAIL"), method = "onDisconnected(Lnet/minecraft/text/Text;)V")
    public void onDisconnected(Text reason, CallbackInfo cbi) {
        PlayerConnectionCallback.LEAVE.invoker().call(player);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream; map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"), method = "onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;)V")
    public Stream<String> onSignUpdate_map(Stream<String> stream, Function<String, String> func) {
        return stream.map(s -> {
            ServerPlayNetworkHandler thiz = ReflectionHelper.cast(this);
            if (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doSignColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel())) s = Command.translateFormats(s);
            else s = Formatting.strip(s);
            return s;
        });
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/ListTag; getString(I)Ljava/lang/String;"), method = "onBookUpdate(Lnet/minecraft/network/packet/c2s/play/BookUpdateC2SPacket;)V")
    public String onBookUpdate_getString(ListTag list, int index) {
        ServerPlayNetworkHandler thiz = ReflectionHelper.cast(this);
        String s = list.getString(index);
        if (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doBookColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel()))
            s = Command.translateFormats(s);
        return s;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils; normalizeSpace(Ljava/lang/String;)Ljava/lang/String;", remap = false), method = "onGameMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V")
    public String onGameMessage_normalizeSpace(String str) {
        ServerPlayNetworkHandler thiz = ReflectionHelper.cast(this);
        String s = StringUtils.normalizeSpace(str);
        if (!str.startsWith("/") && (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doChatColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel())))
            s = Command.translateFormats(s);
        return s;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String; charAt(I)C", remap = false), method = "method_31286(Ljava/lang/String;)V")
    public char method_31286_charAt(String string, int index) {
        ServerPlayNetworkHandler thiz = ReflectionHelper.cast(this);
        char ch = string.charAt(index);
        if (!string.startsWith("/") && ch == '\u00A7' && (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doChatColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel())))
            ch = '&';
        return ch;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String; length()I", remap = false), method = "onRenameItem(Lnet/minecraft/network/packet/c2s/play/RenameItemC2SPacket;)V")
    public int onRenameItem_length(String string) {
        return Objects.requireNonNull(player.world.getGameRules().getBoolean(MoreCommands.doItemColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel()) ? Formatting.strip(MoreCommands.translateFormattings(string)) : string).length();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler; setNewItemName(Ljava/lang/String;)V"), method = "onRenameItem(Lnet/minecraft/network/packet/c2s/play/RenameItemC2SPacket;)V")
    public void onRenameItem_setNewName(AnvilScreenHandler anvil, String name) {
        anvil.setNewItemName(player.world.getGameRules().getBoolean(MoreCommands.doItemColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel()) ? MoreCommands.translateFormattings(name) : name);
    }

}
