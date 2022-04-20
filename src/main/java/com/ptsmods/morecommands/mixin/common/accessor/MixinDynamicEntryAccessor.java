package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DynamicEntry.class)
public interface MixinDynamicEntryAccessor {

	@Accessor
	Identifier getName();
}
