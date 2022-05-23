package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.Rainbow;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public class MixinStyle {
    @Inject(at = @At("RETURN"), method = "getColor()Lnet/minecraft/text/TextColor;")
    public TextColor getColor(CallbackInfoReturnable<TextColor> cbi) {
        return ClientOptions.EasterEggs.rainbows.getValue() && Rainbow.getInstance() != null ? Rainbow.getInstance().RAINBOW_TC : cbi.getReturnValue();
    }
}
