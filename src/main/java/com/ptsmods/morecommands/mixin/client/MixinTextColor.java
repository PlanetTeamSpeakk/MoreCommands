package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Rainbow;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextColor.class)
public class MixinTextColor {

    @Inject(at = @At("RETURN"), method = "getRgb()I")
    public int getRgb(CallbackInfoReturnable<Integer> cbi) {
        return ReflectionHelper.<TextColor>cast(this) == Rainbow.RAINBOW_TC ? Rainbow.getRainbowColour(true) : cbi.getReturnValueI();
    }

}
