package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(at = @At("RETURN"), method = "getHeight()F")
	public final float getHeight(CallbackInfoReturnable<Float> cbi) {
		float height = cbi.getReturnValue();
		if (MoreCommands.isCool(ReflectionHelper.cast(this))) height *= 1.5;
		return height;
	}

}
