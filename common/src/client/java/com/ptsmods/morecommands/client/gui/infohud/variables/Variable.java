package com.ptsmods.morecommands.client.gui.infohud.variables;

import com.mojang.blaze3d.vertex.PoseStack;

public interface Variable<T> {
    String getName();

    T getDefaultValue();

    T fromString(String val);

    void apply(PoseStack matrixStack, Object value);

    void applyDefault(PoseStack matrixStack);

    T upcast(Object value);
}
