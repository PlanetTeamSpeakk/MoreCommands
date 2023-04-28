package com.ptsmods.morecommands.client.mixin.accessor;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface MixinAbstractWidgetAccessor {
    // Since 1.19.3, these fields are accessed via get and set methods.
    // Before 1.19.3, these fields were public and those methods didn't exist.
    @Accessor("y")
    int getY_();

    @Accessor("y")
    void setY_(int y);

    @Accessor("x")
    int getX_();

    @Accessor("x")
    void setX_(int x);
}
