package com.ptsmods.morecommands.mixin.compat.compat17min;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3218;method_15770(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)V", remap = false), method = "method_14171", remap = false)
	private void tickFluid(FluidState state, World world, BlockPos pos) {
		if (ReflectionHelper.<ServerWorld>cast(this).getGameRules().getBoolean(MoreGameRules.doLiquidFlowRule)) state.onScheduledTick(world, pos);
	}
}
