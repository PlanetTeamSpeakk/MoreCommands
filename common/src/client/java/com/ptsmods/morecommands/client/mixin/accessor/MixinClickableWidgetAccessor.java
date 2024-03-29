package com.ptsmods.morecommands.client.mixin.accessor;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface MixinClickableWidgetAccessor {
    @Accessor
    void setHeight(int height);
}
