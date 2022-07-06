package com.ptsmods.morecommands.gui.infohud.variables;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;

public class IntVariable extends AbstractVariable<Integer> {
    private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

    public IntVariable(String name, Integer defaultValue, BiConsumer<MatrixStack, Integer> applicator) {
        super(name, defaultValue, applicator);
    }

    @Override
    public Integer fromString(String val) {
        return MathHelper.clamp(Integer.parseInt(val), min, max);
    }

    @Override
    public Integer upcast(Object value) {
        return (Integer) value;
    }

    public IntVariable clamped(int min, int max) {
        this.min = min;
        this.max = max;

        return this;
    }
}
