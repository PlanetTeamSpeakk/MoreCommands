package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {

    @Shadow public ServerPlayerEntity player;
    @Shadow private @Final MinecraftServer server;

    @Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils; normalizeSpace(Ljava/lang/String;)Ljava/lang/String;", remap = false), method = "onChatMessage(Lnet/minecraft/network/packet/c2s/play/ChatMessageC2SPacket;)V")
    public String onChatMessage_normalizeSpace(String str) {
        String s = StringUtils.normalizeSpace(str);
        if (!s.startsWith("/") && (IMoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), IMoreGameRules.get().doChatColoursRule(), player)
                || player.hasPermissionLevel(server.getOpPermissionLevel()))) s = Util.translateFormats(s);
        return s;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/filter/TextStream$Message;getRaw()Ljava/lang/String;"), method = "handleMessage")
    public String handleMessage_getRaw(TextStream.Message message) {
        return handleFormattings(message.getRaw());
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/filter/TextStream$Message;getFiltered()Ljava/lang/String;"), method = "handleMessage")
    public String handleMessage_getFiltered(TextStream.Message message) {
        return handleFormattings(message.getFiltered());
    }

    private @Unique String handleFormattings(String msg) {
        if (msg == null || msg.isEmpty()) return msg;

        if (!msg.startsWith("/") && (IMoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), IMoreGameRules.get().doChatColoursRule(), player)
                || player.hasPermissionLevel(server.getOpPermissionLevel()))) msg = Util.translateFormats(msg);
        return msg;
    }
}
