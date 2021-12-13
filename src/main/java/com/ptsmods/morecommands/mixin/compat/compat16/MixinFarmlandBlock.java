package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class MixinFarmlandBlock {

	@Inject(at = @At("HEAD"), method = "method_9554", remap = false, cancellable = true)
	private void onLandedUpon(World world, BlockPos pos, Entity entity, float fallDistance, CallbackInfo cbi) {
		if (!MoreGameRules.checkBooleanWithPerm(world.getGameRules(), MoreGameRules.doFarmlandTrampleRule, entity)) cbi.cancel();
	}
}
