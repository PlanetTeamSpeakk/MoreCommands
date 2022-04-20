package com.ptsmods.morecommands.mixin.compat.compat18min;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Inject(at = @At("HEAD"), method = "method_3211", remap = false, cancellable = true)
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo cbi) {
        if (!ClientOptions.Rendering.renderFog.getValue()) cbi.cancel();
    }
}
