package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.AbstractButtonAddon;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractButton.class)
public class MixinAbstractButton implements AbstractButtonAddon {
    private @Unique boolean ignoreKeys;

    @Override
    public void setIgnoreKeys(boolean ignoreKeys) {
        this.ignoreKeys = ignoreKeys;
    }

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    private void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> cbi) {
        if (ignoreKeys) cbi.setReturnValue(false);
    }
}
