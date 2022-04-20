package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
	@Shadow protected ServerWorld world;
	@Final @Shadow protected ServerPlayerEntity player;
	private boolean mc_isFlying = false;

	// Preventing packet from being sent
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendAbilitiesUpdate()V"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
	public void sendAbilitiesUpdate(ServerPlayerEntity player) {}

	@Inject(at = @At("HEAD"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
	public void setGameModePre(GameMode gameMode, GameMode gameMode2, CallbackInfo cbi) {
		mc_isFlying = ((MixinPlayerEntityAccessor) player).getAbilities_().flying; // Making sure you don't fall down while flying when going from creative to survival or when joining.
	}

	@Inject(at = @At("TAIL"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
	public void setGameModePost(GameMode gameMode, GameMode gameMode2, CallbackInfo cbi) {
		// If MAY_FLY is false, let the gamemode decide whether the player may fly or not.
		// If we just straight up set it to the value of MAY_FLY, the player would never be able to fly, not even in creative, when flight is disabled with the /fly command even when you switch gamemode.
		// Spectators can always fly, they'll fall through the map otherwise.
		MixinPlayerEntityAccessor accessor = (MixinPlayerEntityAccessor) player;

		if (player.getDataTracker().get(DataTrackerHelper.MAY_FLY) || gameMode == GameMode.SPECTATOR) accessor.getAbilities_().allowFlying = true;
		if (accessor.getAbilities_().allowFlying) accessor.getAbilities_().flying = mc_isFlying;
		else accessor.getAbilities_().flying = false;
		if (player.getDataTracker().get(DataTrackerHelper.INVULNERABLE)) accessor.getAbilities_().invulnerable = true;
		player.sendAbilitiesUpdate();
	}
}
