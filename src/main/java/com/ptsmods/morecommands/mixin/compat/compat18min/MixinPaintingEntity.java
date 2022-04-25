package com.ptsmods.morecommands.mixin.compat.compat18min;

import com.ptsmods.morecommands.api.addons.PaintingEntityAddon;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PaintingEntity.class)
public class MixinPaintingEntity implements PaintingEntityAddon {


	@Override
	public Object mc$getVariant() {
		return null;
	}

	@Override
	public void mc$setVariant(Object variant) {

	}
}
