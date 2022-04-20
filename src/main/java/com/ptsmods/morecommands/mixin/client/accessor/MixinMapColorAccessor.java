package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.block.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapColor.class)
public interface MixinMapColorAccessor {

	@Accessor("COLORS")
	static MapColor[] getColors() {
		throw new AssertionError("This shouldn't happen!");
	}
}
