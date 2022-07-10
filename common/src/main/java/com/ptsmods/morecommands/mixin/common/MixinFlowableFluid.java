package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
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
public abstract class MixinFlowableFluid {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;canConvertToSource()Z"), method = "getNewLiquid")
    private boolean getUpdatedState_isInfinite(FlowingFluid thiz, LevelReader world, BlockPos pos, BlockState state) {
        return world instanceof Level && ((Level) world).getGameRules().getBoolean(MoreGameRules.get().fluidsInfiniteRule()) || canConvertToSource();
    }

    @Shadow protected abstract boolean canConvertToSource();
}
