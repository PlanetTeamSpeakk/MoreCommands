package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowingFluid.class)
public abstract class MixinFlowingFluid {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;canConvertToSource(Lnet/minecraft/world/level/Level;)Z"), method = "getNewLiquid")
    private boolean getNewLiquid_canConvertToSource(FlowingFluid instance, Level level) {
        return level.getGameRules().getBoolean(IMoreGameRules.get().fluidsInfiniteRule()) || canConvertToSource(level);
    }

    @Shadow
    protected abstract boolean canConvertToSource(Level level);
}
