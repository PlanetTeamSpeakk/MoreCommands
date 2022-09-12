package com.ptsmods.morecommands.mixin.compat.compat16;

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

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String; charAt(I)C", remap = false), method = "handleChat(Ljava/lang/String;)V")
    public char handleChat_charAt(String string, int index) {
        return MixinAccessWidener.get().serverPlayNetworkHandler$gameMsgCharAt(ReflectionHelper.cast(this), string, index, player, server);
    }
}
