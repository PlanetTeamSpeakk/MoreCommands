package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
	@Shadow private int blockBreakingCooldown;
	@Shadow private GameMode gameMode;

	@Inject(at = @At("RETURN"), method = "attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", cancellable = true)
	public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cbi) {
		cbi.setReturnValue(updateAndReturn(cbi));
	}

	@Inject(at = @At("RETURN"), method = "updateBlockBreakingProgress(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", cancellable = true)
	public void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cbi) {
		cbi.setReturnValue(updateAndReturn(cbi));
	}

	@Unique
	private boolean updateAndReturn(CallbackInfoReturnable<Boolean> cbi) {
		if (gameMode.isCreative() && Objects.requireNonNull(MinecraftClient.getInstance().player).getDataTracker().get(DataTrackerHelper.SUPERPICKAXE) &&
				MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof PickaxeItem) blockBreakingCooldown = 0;
		return cbi.getReturnValue();
	}

	@Inject(at = @At("RETURN"), method = "isFlyingLocked()Z", cancellable = true)
	public void isFlyingLocked(CallbackInfoReturnable<Boolean> cbi) {
		cbi.setReturnValue(cbi.getReturnValueZ() || ClientOptions.Tweaks.lockFlying.getValue());
	}
}
