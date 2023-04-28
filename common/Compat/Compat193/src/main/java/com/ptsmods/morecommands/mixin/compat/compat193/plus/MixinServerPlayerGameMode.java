package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {
    // Preventing packet from being sent
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"), method = "setGameModeForPlayer")
    public void sendAbilitiesUpdate(GameType instance, Abilities arg) {}
}
