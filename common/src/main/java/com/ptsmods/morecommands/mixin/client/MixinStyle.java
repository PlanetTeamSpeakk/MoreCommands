package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.Rainbow;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public class MixinStyle {
    @Inject(at = @At("RETURN"), method = "getColor", cancellable = true)
    public void getColor(CallbackInfoReturnable<TextColor> cbi) {
        if (ClientOptions.EasterEggs.rainbows.getValue() && Rainbow.getInstance() != null) cbi.setReturnValue(Rainbow.getInstance().RAINBOW_TC);
    }
}
