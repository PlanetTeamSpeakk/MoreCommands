package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.AbstractWidgetAddon;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public class MixinAbstractWidget implements AbstractWidgetAddon {
    private @Unique IntSet validButtons = IntSet.of(0);
    private @Unique int lastMouseButton;

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
}
