package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface MixinAbstractBlockAccessor {

	@Accessor
	boolean isCollidable();
}
