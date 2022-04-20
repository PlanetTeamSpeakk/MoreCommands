package com.ptsmods.morecommands.mixin.compat.compat18min;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.CompatHolder;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

	@Redirect(at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/class_315;field_1840:D", remap = false), method = "method_3313", remap = false)
	private double update_gamma(GameOptions gameOptions) {
		return ClientOptions.Tweaks.brightnessMultiplier.getValue() * CompatHolder.getClientCompat().getGamma(gameOptions);
	}
}
