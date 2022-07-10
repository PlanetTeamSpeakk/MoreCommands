package com.ptsmods.morecommands.gui.infohud.variables;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;

public class DoubleVariable extends AbstractVariable<Double> {
    public DoubleVariable(String name, Double defaultValue, BiConsumer<PoseStack, Double> applicator) {
        super(name, defaultValue, applicator);
    }

    @Override
    public Double fromString(String val) {
        return Double.parseDouble(val);
    }

    @Override
    public Double upcast(Object value) {
        return (Double) value;
    }
}
