package com.ptsmods.morecommands.mixin.compat.compat182.plus;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(at = @At("HEAD"), method = "canWalkOnFluid", cancellable = true)
    public void canWalkOnFluid(FluidState fluidState, CallbackInfoReturnable<Boolean> cbi) {
        if (ReflectionHelper.<LivingEntity>cast(this) instanceof PlayerEntity && ReflectionHelper.<LivingEntity>cast(this).getDataTracker().get(IDataTrackerHelper.get().jesus()))
            cbi.setReturnValue(true);
    }
}
