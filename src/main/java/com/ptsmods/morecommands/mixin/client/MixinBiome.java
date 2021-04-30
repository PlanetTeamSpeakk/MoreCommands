package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Rainbow;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class MixinBiome {

	@Inject(at = @At("RETURN"), method = "getSkyColor()I")
	public int getSkyColor(CallbackInfoReturnable<Integer> cbi) {
		return getColour(cbi);
	}

	@Inject(at = @At("RETURN"), method = "getFogColor()I")
	public int getFogColor(CallbackInfoReturnable<Integer> cbi) {
		return getColour(cbi);
	}

	@Inject(at = @At("RETURN"), method = "getWaterFogColor()I")
	public int getWaterFogColor(CallbackInfoReturnable<Integer> cbi) {
		return getColour(cbi);
	}

	private int getColour(CallbackInfoReturnable<Integer> cbi) {
		return ClientOptions.EasterEggs.rainbows.getValue() && Rainbow.getInstance() != null ? Rainbow.getInstance().getRainbowColour(false) : cbi.getReturnValueI();
	}

}
