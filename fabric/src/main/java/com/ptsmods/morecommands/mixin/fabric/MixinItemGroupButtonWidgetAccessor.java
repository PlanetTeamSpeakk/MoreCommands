package com.ptsmods.morecommands.mixin.fabric;

import net.fabricmc.fabric.impl.client.item.group.FabricCreativeGuiComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public interface MixinItemGroupButtonWidgetAccessor {
    @Accessor(remap = false)
    FabricCreativeGuiComponents.Type getType();
}
