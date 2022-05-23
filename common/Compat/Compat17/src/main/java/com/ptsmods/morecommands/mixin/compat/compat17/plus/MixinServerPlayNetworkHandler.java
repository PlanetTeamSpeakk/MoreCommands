package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayerEntity player;

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false), method = "onGameMessage")
    public char onGameMessage_charAt(String string, int index) {
        return MixinAccessWidener.get().serverPlayNetworkHandler$gameMsgCharAt(ReflectionHelper.cast(this), string, index, player, server);
    }
}
