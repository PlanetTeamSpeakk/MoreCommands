package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.util.Rainbow;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextColor.class)
public class MixinTextColor {

	@Inject(at = @At("RETURN"), method = "getRgb()I")
	public int getRgb(CallbackInfoReturnable<Integer> cbi) {
		return Rainbow.getInstance() != null && ReflectionHelper.<TextColor>cast(this) == Rainbow.getInstance().RAINBOW_TC ? Rainbow.getInstance().getRainbowColour(true) : cbi.getReturnValueI();
	}

}
