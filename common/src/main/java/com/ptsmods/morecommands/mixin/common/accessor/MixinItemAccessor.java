package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface MixinItemAccessor {
    @Accessor @Mutable void setCategory(CreativeModeTab group);
}
