package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClickableWidget.class)
public interface MixinClickableWidgetAccessor {
    @Accessor
    void setHeight(int height);
}
