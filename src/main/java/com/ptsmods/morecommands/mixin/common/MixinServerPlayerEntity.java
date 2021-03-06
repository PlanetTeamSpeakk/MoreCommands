package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
	@Unique private int lastPing = -1;

	/**
	 * @author PlanetTeamSpeak
	 */
	@Nullable
	@Overwrite
	public Text getPlayerListName() {
		return ReflectionHelper.<ServerPlayerEntity>cast(this).getDisplayName();
	}

	@Inject(at = @At("HEAD"), method = "playerTick()V")
	public void playerTick(CallbackInfo cbi) {
		ServerPlayerEntity thiz = ReflectionHelper.cast(this);
		if (thiz.pingMilliseconds != lastPing) {
			lastPing = thiz.pingMilliseconds;
			updateScores(MoreCommands.LATENCY, lastPing);
		}
	}

	@Shadow private void updateScores(ScoreboardCriterion criterion, int score) {
		throw new AssertionError("This should not happen.");
	}

	// First changing world, then teleporting to different position seeing as that's how it's done naturally when going through a portal.
	// Normal behaviour breaks the back command when using it twice in a row after going through a portal.
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;refreshPositionAndAngles(DDDFF)V"), method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V")
	private void teleport_refreshPositionAndAngles(ServerPlayerEntity thiz, double x, double y, double z, float yaw, float pitch, ServerWorld targetWorld, double x0, double y0, double z0, float yaw0, float pitch0) {
		Compat.getCompat().playerSetWorld(thiz, targetWorld);
	}
}
