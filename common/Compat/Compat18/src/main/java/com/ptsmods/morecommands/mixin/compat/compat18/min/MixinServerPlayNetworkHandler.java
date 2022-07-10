package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.UUID;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerPlayNetworkHandler {

    @Shadow public ServerPlayer player;
    @Shadow private @Final MinecraftServer server;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"), method = "onDisconnect")
    public void onDisconnected_broadcastChatMessage(PlayerList playerManager, Component msg, ChatType type, UUID id) {
        if (IMoreGameRules.get().checkBooleanWithPerm(Objects.requireNonNull(playerManager.getServer().getLevel(Level.OVERWORLD)).getGameRules(), IMoreGameRules.get().doJoinMessageRule(), player)
                && !player.getEntityData().get(IDataTrackerHelper.get().vanish())) Compat.get().broadcast(playerManager, new Tuple<>(type.ordinal(), null), msg);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils; normalizeSpace(Ljava/lang/String;)Ljava/lang/String;", remap = false), method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V")
    public String onChatMessage_normalizeSpace(String str) {
        String s = StringUtils.normalizeSpace(str);
        if (!s.startsWith("/") && (IMoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), IMoreGameRules.get().doChatColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()))) s = Util.translateFormats(s);
        return s;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/TextFilter$FilteredText;getRaw()Ljava/lang/String;"), method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V")
    public String handleMessage_getRaw(TextFilter.FilteredText message) {
        return handleFormattings(message.getRaw());
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/TextFilter$FilteredText;getFiltered()Ljava/lang/String;"), method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V")
    public String handleMessage_getFiltered(TextFilter.FilteredText message) {
        return handleFormattings(message.getFiltered());
    }

    private @Unique String handleFormattings(String msg) {
        if (msg == null || msg.isEmpty()) return msg;

        if (!msg.startsWith("/") && (IMoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), IMoreGameRules.get().doChatColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()))) msg = Util.translateFormats(msg);
        return msg;
    }
}
