package com.ptsmods.morecommands.mixin.compat.compat182.plus;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(at = @At("HEAD"), method = "canStandOnFluid", cancellable = true)
    public void canWalkOnFluid(FluidState fluidState, CallbackInfoReturnable<Boolean> cbi) {
        if (ReflectionHelper.<LivingEntity>cast(this) instanceof Player &&
                ReflectionHelper.<LivingEntity>cast(this).getEntityData().get(IDataTrackerHelper.get().jesus()))
            cbi.setReturnValue(true);
    }
}
