package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface MixinItemAccessor {
    @Accessor @Mutable void setGroup(ItemGroup group);
}
