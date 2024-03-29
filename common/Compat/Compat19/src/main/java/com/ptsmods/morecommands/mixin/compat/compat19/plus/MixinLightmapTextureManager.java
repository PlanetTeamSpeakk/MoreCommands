package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import com.ptsmods.morecommands.api.clientoptions.DoubleClientOption;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightTexture.class)
public class MixinLightmapTextureManager {

    @ModifyVariable(at = @At("STORE"), ordinal = 8, method = "updateLightTexture")
    public float storeGamma(float gamma) {
        return (float) (((DoubleClientOption) ClientOption.getOptions().get(ClientOptionCategory.TWEAKS).get("Brightness Multiplier")).getValue() * gamma);
    }
}
