package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.Rainbow;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class MixinBiome {

    @Inject(at = @At("RETURN"), method = "getSkyColor()I", cancellable = true)
    public void getSkyColor(CallbackInfoReturnable<Integer> cbi) {
        cbi.setReturnValue(getColour(cbi));
    }

    @Inject(at = @At("RETURN"), method = "getFogColor()I", cancellable = true)
    public void getFogColor(CallbackInfoReturnable<Integer> cbi) {
        cbi.setReturnValue(getColour(cbi));
    }

    @Inject(at = @At("RETURN"), method = "getWaterFogColor()I", cancellable = true)
    public void getWaterFogColor(CallbackInfoReturnable<Integer> cbi) {
        cbi.setReturnValue(getColour(cbi));
    }

    private int getColour(CallbackInfoReturnable<Integer> cbi) {
        return ClientOptions.EasterEggs.rainbows.getValue() && Rainbow.getInstance() != null ? Rainbow.getInstance().getRainbowColour(false) : cbi.getReturnValueI();
    }
}
