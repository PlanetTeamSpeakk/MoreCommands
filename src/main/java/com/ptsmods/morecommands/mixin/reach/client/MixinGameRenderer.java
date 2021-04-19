package com.ptsmods.morecommands.mixin.reach.client;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	@Shadow @Final private MinecraftClient client;

	@ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "updateTargetedEntity(F)V")
	public double updateTargetedEntity_maxReach(double maxReach) {
		return ReachCommand.getReach(Objects.requireNonNull(client.player), false);
	}
}
