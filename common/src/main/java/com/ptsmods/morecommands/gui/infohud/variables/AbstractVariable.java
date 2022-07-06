package com.ptsmods.morecommands.gui.infohud.variables;

import net.minecraft.client.util.math.MatrixStack;

import java.util.function.BiConsumer;

abstract class AbstractVariable<T> implements Variable<T> {
    protected final String name;
    protected final T defaultValue;
    private final BiConsumer<MatrixStack, T> applicator;

    public AbstractVariable(String name, T defaultValue, BiConsumer<MatrixStack, T> applicator) {
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
    public void apply(MatrixStack matrixStack, Object value) {
        applicator.accept(matrixStack, upcast(value));
    }

    @Override
    public void applyDefault(MatrixStack matrixStack) {
        applicator.accept(matrixStack, getDefaultValue());
    }
}
