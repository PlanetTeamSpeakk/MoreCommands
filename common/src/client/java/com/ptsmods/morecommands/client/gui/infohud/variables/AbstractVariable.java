package com.ptsmods.morecommands.client.gui.infohud.variables;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;

abstract class AbstractVariable<T> implements Variable<T> {
    protected final String name;
    protected final T defaultValue;
    private final BiConsumer<PoseStack, T> applicator;

    public AbstractVariable(String name, T defaultValue, BiConsumer<PoseStack, T> applicator) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.applicator = applicator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void apply(PoseStack matrixStack, Object value) {
        applicator.accept(matrixStack, upcast(value));
    }

    @Override
    public void applyDefault(PoseStack matrixStack) {
        applicator.accept(matrixStack, getDefaultValue());
    }
}
