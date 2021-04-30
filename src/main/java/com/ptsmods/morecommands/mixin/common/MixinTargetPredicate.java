package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetPredicate.class)
public class MixinTargetPredicate {
    @Inject(at = @At("HEAD"), method = "test", cancellable = true)
    public void test(LivingEntity baseEntity, LivingEntity targetEntity, CallbackInfoReturnable<Boolean> cbi) {
        if (targetEntity instanceof PlayerEntity && targetEntity.getDataTracker().get(MoreCommands.VANISH)) cbi.setReturnValue(false);
    }
}
