package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import com.ptsmods.morecommands.api.clientoptions.DoubleClientOption;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

	@Redirect(at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/option/GameOptions;gamma:D"), method = "update")
	private double update_gamma(GameOptions gameOptions) {
		return ((DoubleClientOption) ClientOption.getOptions().get(ClientOptionCategory.TWEAKS).get("Brightness Multiplier")).getValue() * ClientCompat.get().getGamma(gameOptions);
	}
}
