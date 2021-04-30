package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Rainbow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
	@Shadow private String splashText;

	@Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Random; nextFloat()F", remap = false), method = "<init>(Z)V")
	private float init_nextFloat(Random random) {
		return ClientOptions.Tweaks.alwaysMinceraft.getValue() ? 0f : random.nextFloat();
	}

	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo cbi) { // Couldn't get it to work with ModifyVariable for whatever reason.
		if (splashText == null) splashText = MinecraftClient.getInstance().getSplashTextLoader().get();
		if (splashText != null) splashText = ClientOptions.Tweaks.rainbowSplash.getValue() && Rainbow.getInstance() != null ? Rainbow.getInstance().RAINBOW + splashText : splashText;
	}
}
