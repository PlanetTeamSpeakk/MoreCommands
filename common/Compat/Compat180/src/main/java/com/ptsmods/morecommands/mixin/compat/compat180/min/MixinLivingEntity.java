package com.ptsmods.morecommands.mixin.compat.compat180.min;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Group(name = "canWalkOnFluid", min = 1, max = 1)
    @Inject(at = @At("HEAD"), method = "canStandOnFluid", cancellable = true)
    public void canWalkOnFluid(Fluid fluid, CallbackInfoReturnable<Boolean> cbi) {
        if (ReflectionHelper.<LivingEntity>cast(this) instanceof Player &&
                ReflectionHelper.<LivingEntity>cast(this).getEntityData().get(IDataTrackerHelper.get().jesus()))
            cbi.setReturnValue(true);
    }
}
