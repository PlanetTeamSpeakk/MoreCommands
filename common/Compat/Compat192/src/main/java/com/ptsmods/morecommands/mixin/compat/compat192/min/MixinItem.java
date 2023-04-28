package com.ptsmods.morecommands.mixin.compat.compat192.min;

import com.ptsmods.morecommands.api.addons.ItemTabAddon;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public class MixinItem implements ItemTabAddon {

    @Shadow @Final @Mutable
    protected CreativeModeTab category;

    @Override
    public void setTab(CreativeModeTab tab) {
        category = tab;
    }
}
