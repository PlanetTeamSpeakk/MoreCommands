package com.ptsmods.morecommands.mixin.compat.compat17;

import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class MixinServerWorld {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), method = "tickLiquid")
    private void tickFluid(FluidState state, Level world, BlockPos pos) {
        if (ReflectionHelper.<ServerLevel>cast(this).getGameRules().getBoolean(IMoreGameRules.get().doLiquidFlowRule())) state.tick(world, pos);
    }
}
