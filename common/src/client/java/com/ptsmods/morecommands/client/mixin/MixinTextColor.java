package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.util.Rainbow;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextColor.class)
public class MixinTextColor {

    @Inject(at = @At("RETURN"), method = "getValue", cancellable = true)
    public void getRgb(CallbackInfoReturnable<Integer> cbi) {
        if (Rainbow.getInstance() != null && ReflectionHelper.<TextColor>cast(this) == Rainbow.getInstance().RAINBOW_TC)
            cbi.setReturnValue(Rainbow.getInstance().getRainbowColour(true));
    }
}
