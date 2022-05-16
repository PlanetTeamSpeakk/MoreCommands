package com.ptsmods.morecommands.mixin.fabric;

import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public interface MixinItemGroupButtonWidgetAccessor {
	@Accessor(remap = false)
	FabricCreativeGuiComponents.Type getType();
}
