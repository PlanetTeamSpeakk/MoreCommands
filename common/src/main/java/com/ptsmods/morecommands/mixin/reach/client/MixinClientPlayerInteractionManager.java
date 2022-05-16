package com.ptsmods.morecommands.mixin.reach.client;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
	@Shadow @Final private MinecraftClient client;

	@Inject(at = @At("RETURN"), method = "getReachDistance()F", cancellable = true)
	public void getReachDistance(CallbackInfoReturnable<Float> cbi) {
		cbi.setReturnValue((float) ReachCommand.getReach(Objects.requireNonNull(client.player), false));
	}
}
