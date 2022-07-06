package com.ptsmods.morecommands.gui.infohud.variables;

import net.minecraft.client.util.math.MatrixStack;

public interface Variable<T> {
    String getName();

    T getDefaultValue();

    T fromString(String val);

    void apply(MatrixStack matrixStack, Object value);

    void applyDefault(MatrixStack matrixStack);

    T upcast(Object value);
}
