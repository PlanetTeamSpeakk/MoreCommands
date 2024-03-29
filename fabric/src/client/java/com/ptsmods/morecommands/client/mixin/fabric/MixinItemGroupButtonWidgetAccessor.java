package com.ptsmods.morecommands.client.mixin.fabric;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public interface MixinItemGroupButtonWidgetAccessor {
    @Accessor(remap = false)
    FabricCreativeGuiComponents.Type getType();
}
