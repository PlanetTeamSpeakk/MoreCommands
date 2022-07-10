package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.callbacks.KeyEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboard {

    @Inject(at = @At("HEAD"), method = "keyPress", cancellable = true)
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo cbi) {
        if (window == Minecraft.getInstance().getWindow().getWindow() && KeyEvent.EVENT.invoker().onKey(key, scancode, i, j)) cbi.cancel();
    }
}
