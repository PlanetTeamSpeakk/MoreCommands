package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class MixinAbstractClientPlayerEntity {

	@Inject(at = @At("RETURN"), method = "getCapeTexture", cancellable = true)
	private void getCapeTexture(CallbackInfoReturnable<Identifier> cbi) {
		if (MoreCommands.isCool(ReflectionHelper.cast(this)))
			cbi.setReturnValue(new Identifier("morecommands:textures/cape.png"));
	}
}
