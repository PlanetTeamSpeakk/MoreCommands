package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow public ServerWorld world;
    @Shadow public ServerPlayerEntity player;
    private boolean mc_isFlying = false;

    // Preventing packet from being sent
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity; sendAbilitiesUpdate()V"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
    public void sendAbilitiesUpdate(ServerPlayerEntity player) {}

    @Inject(at = @At("HEAD"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
    public void setGameModePre(GameMode gameMode, GameMode gameMode2, CallbackInfo cbi) {
        mc_isFlying = player.abilities.flying; // Making sure you don't fall down while flying when going from creative to survival or when joining.
    }

    @Inject(at = @At("TAIL"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
    public void setGameModePost(GameMode gameMode, GameMode gameMode2, CallbackInfo cbi) {
        // If MAY_FLY is false, let the gamemode decide whether the player may fly or not.
        // If we just straight up set it to the value of MAY_FLY, the player would never be able to fly, not even in creative, when flight is disabled with the /fly command even when you switch gamemode.
        // Spectators can always fly, they'll fall through the map otherwise.
        if (player.getDataTracker().get(MoreCommands.MAY_FLY) || gameMode == GameMode.SPECTATOR) player.abilities.allowFlying = true;
        if (player.abilities.allowFlying) player.abilities.flying = mc_isFlying;
        else player.abilities.flying = false;
        if (player.getDataTracker().get(MoreCommands.INVULNERABLE)) player.abilities.invulnerable = true;
        player.sendAbilitiesUpdate();
    }

}
