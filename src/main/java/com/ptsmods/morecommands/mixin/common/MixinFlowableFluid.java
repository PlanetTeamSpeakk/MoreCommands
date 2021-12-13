package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowableFluid.class)
public abstract class MixinFlowableFluid {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FlowableFluid; isInfinite()Z"), method = "getUpdatedState(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/fluid/FluidState;")
	private boolean getUpdatedState_isInfinite(FlowableFluid thiz, WorldView world, BlockPos pos, BlockState state) {
		return world instanceof World && ((World) world).getGameRules().getBoolean(MoreGameRules.fluidsInfiniteRule) || callIsInfinite();
	}

	@Invoker
	public abstract boolean callIsInfinite();

}
