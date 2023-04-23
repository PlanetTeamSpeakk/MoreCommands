package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.Rainbow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @Shadow private String splash;

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo cbi) { // Couldn't get it to work with ModifyVariable for whatever reason.
        if (splash == null) splash = Minecraft.getInstance().getSplashManager().getSplash();
        if (splash != null) splash = ClientOptions.Tweaks.rainbowSplash.getValue() && Rainbow.getInstance() != null ? Rainbow.getInstance().RAINBOW + splash : splash;
    }
}
