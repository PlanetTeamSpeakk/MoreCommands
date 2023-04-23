package com.ptsmods.morecommands.client.gui.infohud.variables;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;
import net.minecraft.util.Mth;

public class IntVariable extends AbstractVariable<Integer> {
    private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

    public IntVariable(String name, Integer defaultValue, BiConsumer<PoseStack, Integer> applicator) {
        super(name, defaultValue, applicator);
    }

    @Override
    public Integer fromString(String val) {
        return Mth.clamp(Integer.parseInt(val), min, max);
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
