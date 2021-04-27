package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.callbacks.KeyCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

	@Inject(at = @At("HEAD"), method = "onKey(JIIII)V", cancellable = true)
	public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo cbi) {
		if (window == MinecraftClient.getInstance().getWindow().getHandle() && KeyCallback.EVENT.invoker().onKey(key, scancode, i, j)) cbi.cancel();
	}

}
