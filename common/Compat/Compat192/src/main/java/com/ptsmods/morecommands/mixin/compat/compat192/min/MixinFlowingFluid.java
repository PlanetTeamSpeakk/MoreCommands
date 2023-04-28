package com.ptsmods.morecommands.mixin.compat.compat192.min;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowingFluid.class)
public abstract class MixinFlowingFluid {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;canConvertToSource()Z"), method = "getNewLiquid")
    private boolean getNewLiquid_canConvertToSource(FlowingFluid thiz, LevelReader world, BlockPos pos, BlockState state) {
        return world instanceof Level && ((Level) world).getGameRules().getBoolean(IMoreGameRules.get().fluidsInfiniteRule()) || canConvertToSource();
    }

    @Shadow
    protected abstract boolean canConvertToSource();
}
