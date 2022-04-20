package com.ptsmods.morecommands.mixin.compat.compat19plus;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowBlock.class)
public class MixinSnowBlock {

	@Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random, CallbackInfo cbi) {
		if (!world.getGameRules().getBoolean(MoreGameRules.doMeltRule)) cbi.cancel();
	}
}
