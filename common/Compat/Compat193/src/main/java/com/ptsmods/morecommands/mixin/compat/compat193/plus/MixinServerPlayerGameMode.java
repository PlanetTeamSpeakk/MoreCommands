package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {
    // Preventing packet from being sent
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;onUpdateAbilities()V"), method = "changeGameModeForPlayer")
    public void sendAbilitiesUpdate(ServerPlayer instance) {}
}
