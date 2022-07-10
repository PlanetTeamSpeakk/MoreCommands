package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class MixinPlayerEntity {
    @Inject(at = @At("RETURN"), method = "getBlockSpeedFactor", cancellable = true)
    protected void getVelocityMultiplier(CallbackInfoReturnable<Float> cbi) {
        Player thiz = ReflectionHelper.cast(this);
        cbi.setReturnValue(thiz instanceof LocalPlayer && ((LocalPlayer) thiz).input.forwardImpulse == 0 && ((LocalPlayer) thiz).input.leftImpulse == 0 && ClientOptions.Tweaks.immediateMoveStop.getValue() ? 0f : cbi.getReturnValueF());
    }
}