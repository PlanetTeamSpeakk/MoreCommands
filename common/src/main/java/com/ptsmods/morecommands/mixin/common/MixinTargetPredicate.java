package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public class MixinTargetPredicate {
    @Inject(at = @At("HEAD"), method = "test", cancellable = true)
    public void test(LivingEntity baseEntity, LivingEntity targetEntity, CallbackInfoReturnable<Boolean> cbi) {
        if (targetEntity instanceof Player && targetEntity.getEntityData().get(IDataTrackerHelper.get().vanish())) cbi.setReturnValue(false);
    }
}
