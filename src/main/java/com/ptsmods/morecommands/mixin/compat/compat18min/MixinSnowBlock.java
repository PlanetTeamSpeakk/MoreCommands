package com.ptsmods.morecommands.mixin.compat.compat18min;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SnowBlock.class)
public class MixinSnowBlock {

	@Inject(at = @At("HEAD"), method = "method_9514", remap = false, cancellable = true)
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo cbi) {
		if (!world.getGameRules().getBoolean(MoreGameRules.doMeltRule)) cbi.cancel();
	}
}
