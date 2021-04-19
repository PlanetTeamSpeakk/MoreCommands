package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

	@Inject(at = @At("RETURN"), method = "getVelocityMultiplier()F")
	protected float getVelocityMultiplier(CallbackInfoReturnable<Float> cbi) {
		PlayerEntity thiz = ReflectionHelper.cast(this);
		return thiz instanceof ClientPlayerEntity && ((ClientPlayerEntity) thiz).input.movementForward == 0 && ((ClientPlayerEntity) thiz).input.movementSideways == 0 && ClientOptions.Tweaks.immediateMoveStop.getValue() ? 0f : cbi.getReturnValueF();
	}

}
