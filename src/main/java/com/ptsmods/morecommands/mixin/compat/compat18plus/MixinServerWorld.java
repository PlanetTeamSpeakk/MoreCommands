package com.ptsmods.morecommands.mixin.compat.compat18plus;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;onScheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), method = "tickFluid")
    private void tickFluid(FluidState state, World world, BlockPos pos) {
        if (ReflectionHelper.<ServerWorld>cast(this).getGameRules().getBoolean(MoreGameRules.doLiquidFlowRule)) state.onScheduledTick(world, pos);
    }
}
