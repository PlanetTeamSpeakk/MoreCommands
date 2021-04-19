package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

	private int mc_lastPing = -1;

	@Overwrite
	public Text getPlayerListName() {
		return ReflectionHelper.<ServerPlayerEntity>cast(this).getDisplayName();
	}

	@Inject(at = @At("HEAD"), method = "playerTick()V")
	public void playerTick(CallbackInfo cbi) {
		ServerPlayerEntity thiz = ReflectionHelper.cast(this);
		if (thiz.pingMilliseconds != mc_lastPing) {
			mc_lastPing = thiz.pingMilliseconds;
			updateScores(MoreCommands.LATENCY, mc_lastPing);
		}
	}

	@Shadow
	private void updateScores(ScoreboardCriterion criterion, int score) {
		throw new AssertionError("This should not happen.");
	}

	// First changing world, then teleporting to different position seeing as that's how it's done naturally when going through a portal.
	// Normal behaviour breaks the back command when using it twice in a row after going through a portal.
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity; refreshPositionAndAngles(DDDFF)V"), method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V")
	private void teleport_refreshPositionAndAngles(ServerPlayerEntity thiz, double x, double y, double z, float yaw, float pitch, ServerWorld targetWorld, double x0, double y0, double z0, float yaw0, float pitch0) {
		thiz.setWorld(targetWorld);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity; setWorld(Lnet/minecraft/world/World;)V"), method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V")
	private void teleport_setWorld(ServerPlayerEntity thiz, World targetWorld, ServerWorld targetWorld0, double x, double y, double z, float yaw, float pitch) {
		thiz.refreshPositionAndAngles(x, y, z, yaw, pitch);
	}

}
