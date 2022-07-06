package com.ptsmods.morecommands.gui.infohud.variables;

import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.function.BiConsumer;

public class ColourVariable extends AbstractVariable<Color> {

    public ColourVariable(String name, Color defaultValue, BiConsumer<MatrixStack, Color> applicator) {
        super(name, defaultValue, applicator);
    }

    @Override
    public Color fromString(String val) {
        return new Color(Integer.parseInt(val.startsWith("#") ? val.substring(1) : val, 16));
    }

    @Override
    public Color upcast(Object value) {
        return (Color) value;
    }
}
