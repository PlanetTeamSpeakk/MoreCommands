package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Inject(at = @At("RETURN"), method = "getVelocityMultiplier()F", cancellable = true)
    protected void getVelocityMultiplier(CallbackInfoReturnable<Float> cbi) {
        PlayerEntity thiz = ReflectionHelper.cast(this);
        cbi.setReturnValue(thiz instanceof ClientPlayerEntity && ((ClientPlayerEntity) thiz).input.movementForward == 0 && ((ClientPlayerEntity) thiz).input.movementSideways == 0 && ClientOptions.Tweaks.immediateMoveStop.getValue() ? 0f : cbi.getReturnValueF());
    }
}