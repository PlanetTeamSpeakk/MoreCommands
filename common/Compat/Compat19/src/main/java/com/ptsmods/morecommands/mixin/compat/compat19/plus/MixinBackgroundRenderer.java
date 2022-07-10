package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class MixinBackgroundRenderer {

    @Inject(at = @At("HEAD"), method = "setupFog", cancellable = true)
    private static void applyFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float f, CallbackInfo cbi) {
        if (!((BooleanClientOption) ClientOption.getOptions().get(ClientOptionCategory.RENDERING).get("Render Fog")).getValue()) cbi.cancel();
    }
}
