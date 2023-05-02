package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.AbstractWidgetAddon;
import com.ptsmods.morecommands.api.addons.ScalableWidget;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public class MixinAbstractWidget implements AbstractWidgetAddon, ScalableWidget {
    private @Unique IntSet validButtons = IntSet.of(0);
    private @Unique int lastMouseButton;
    private @Unique boolean autoScale = false, focusable = true;

    @Override
    public void setValidButtons(int... buttons) {
        validButtons = IntSet.of(buttons);
    }

    @Override
    public int getLastMouseButton() {
        return lastMouseButton;
    }

    @Inject(at = @At("HEAD"), method = "isValidClickButton", cancellable = true)
    private void isValidClickButton(int btn, CallbackInfoReturnable<Boolean> cbi) {
        lastMouseButton = btn;
        cbi.setReturnValue(validButtons.contains(btn));
    }

    @Override
    public void setAutoScale(boolean autoScale) {
        this.autoScale = autoScale;
    }

    @Override
    public boolean isAutoScale() {
        return autoScale;
    }

    @Override
    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    @Inject(method = "setFocused", at = @At("HEAD"), cancellable = true)
    private void cancelFocusRequest(boolean bl, CallbackInfo ci) {
        if (!focusable) ci.cancel();
    }
}
