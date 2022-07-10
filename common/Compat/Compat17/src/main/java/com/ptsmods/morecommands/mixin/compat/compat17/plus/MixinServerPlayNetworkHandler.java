package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerPlayNetworkHandler {
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayer player;

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false), method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V", require = 0)
    public char onGameMessage_charAt(String string, int index) {
        return MixinAccessWidener.get().serverPlayNetworkHandler$gameMsgCharAt(ReflectionHelper.cast(this), string, index, player, server);
    }
}
