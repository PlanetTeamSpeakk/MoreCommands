package com.ptsmods.morecommands.gui.infohud.variables;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.*;
import java.util.function.BiConsumer;

public class ColourVariable extends AbstractVariable<Color> {

    public ColourVariable(String name, Color defaultValue, BiConsumer<PoseStack, Color> applicator) {
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
