package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTable.class)
public interface MixinLootTableAccessor {

    @Accessor
    LootPool[] getPools();
}
