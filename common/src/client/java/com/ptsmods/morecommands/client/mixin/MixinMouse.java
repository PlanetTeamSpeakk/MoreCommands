package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.callbacks.MouseEvent;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouse {
    @Inject(at = @At("HEAD"), method = "onPress", cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo cbi) {
        if (MouseEvent.EVENT.invoker().onMouse(button, action, mods)) cbi.cancel();
    }
}
