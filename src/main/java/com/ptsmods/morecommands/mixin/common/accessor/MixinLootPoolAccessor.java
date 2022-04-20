package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface MixinLootPoolAccessor {

	@Accessor
	LootPoolEntry[] getEntries();

	@Accessor
	LootCondition[] getConditions();
}
