package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayer player;

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false), method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V", require = 0)
    public char onGameMessage_charAt(String string, int index) {
        return MixinAccessWidener.get().serverPlayNetworkHandler$gameMsgCharAt(ReflectionHelper.cast(this), string, index, player, server);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;limit(J)Ljava/util/stream/Stream;", remap = false), method = "handleEditBook")
    public Stream<String> onBookUpdate_limit(Stream<String> stream, long maxSize) {
        return IMoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), IMoreGameRules.get().doBookColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()) ? stream.limit(maxSize).map(Util::translateFormats) : stream.limit(maxSize);
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
