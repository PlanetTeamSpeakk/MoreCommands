package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public class MixinLogoRenderer {

    @Shadow @Final @Mutable
    private boolean showEasterEgg;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init_easterEgg(boolean keepLogoThroughFade, CallbackInfo ci) {
        if (ClientOption.getBoolean("alwaysMinceraft"))
            showEasterEgg = true;
    }
}
