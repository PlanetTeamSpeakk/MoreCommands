package com.ptsmods.morecommands.mixin.common;

import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowingFluid.class)
public abstract class MixinFlowableFluid {

    // TODO
//    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;canConvertToSource()Z"), method = "getNewLiquid")
//    private boolean getNewLiquid_canConvertToSource(FlowingFluid thiz, LevelReader world, BlockPos pos, BlockState state) {
//        return world instanceof Level && ((Level) world).getGameRules().getBoolean(MoreGameRules.get().fluidsInfiniteRule()) || canConvertToSource();
//    }
//
//    @Shadow protected abstract boolean canConvertToSource();
}
