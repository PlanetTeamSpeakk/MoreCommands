package com.ptsmods.morecommands.mixin.compat.compat19plus;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

	@ModifyVariable(at = @At("STORE"), ordinal = 8, method = "update")
	public float storeGamma(float gamma) {
		return (float) (ClientOptions.Tweaks.brightnessMultiplier.getValue() * gamma);
	}
}
