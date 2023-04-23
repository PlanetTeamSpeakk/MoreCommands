package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class MixinClientPlayerEntityTweakerooCompat {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;isPauseScreen()Z"), method = "handleNetherPortalClient")
    private boolean updateNausea_isPauseScreen(Screen s) {
        return ClientOptions.Tweaks.screensInPortal.getValue() || s.isPauseScreen();
    }
}
