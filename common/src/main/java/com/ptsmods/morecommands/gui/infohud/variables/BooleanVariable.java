package com.ptsmods.morecommands.gui.infohud.variables;

import net.minecraft.client.util.math.MatrixStack;

import java.util.function.BiConsumer;

public class BooleanVariable extends AbstractVariable<Boolean> {
    public BooleanVariable(String name, Boolean defaultValue, BiConsumer<MatrixStack, Boolean> applicator) {
        super(name, defaultValue, applicator);
    }

    @Override
    public Boolean fromString(String val) {
        return "true".equalsIgnoreCase(val);
    }

    @Override
    public Boolean upcast(Object value) {
        return (Boolean) value;
    }
}
