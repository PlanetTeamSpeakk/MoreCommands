package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DynamicLoot.class)
public interface MixinDynamicEntryAccessor {

    @Accessor
    ResourceLocation getName();
}
