package com.ptsmods.morecommands.client.gui.infohud.variables;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;

public class BooleanVariable extends AbstractVariable<Boolean> {
    public BooleanVariable(String name, Boolean defaultValue, BiConsumer<PoseStack, Boolean> applicator) {
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
