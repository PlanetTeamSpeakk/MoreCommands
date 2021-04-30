package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractButtonWidget.class)
public interface MixinAbstractButtonWidgetAccessor {
    @Accessor
    void setHeight(int height);
}
