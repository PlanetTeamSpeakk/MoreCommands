package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import java.text.DecimalFormat;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class DoubleClientOption extends ClientOption<Double> {
    private final double min, max;
    private static final DecimalFormat format = new DecimalFormat("#.##");

    public DoubleClientOption(ClientOptionCategory category, String name, double defaultValue, double min, double max) {
        super(category, name, defaultValue);
        this.min = min;
        this.max = max;
    }

    public DoubleClientOption(ClientOptionCategory category, String name, double defaultValue, double min, double max, BiConsumer<Double, Double> updateConsumer) {
        super(category, name, defaultValue, updateConsumer);
        this.min = min;
        this.max = max;
    }

    public DoubleClientOption(ClientOptionCategory category, String name, double defaultValue, double min, double max, String... comments) {
        super(category, name, defaultValue, comments);
        this.min = min;
        this.max = max;
    }

    public DoubleClientOption(ClientOptionCategory category, String name, double defaultValue, double min, double max, BiConsumer<Double, Double> updateConsumer, String... comments) {
        super(category, name, defaultValue, updateConsumer, comments);
        this.min = min;
        this.max = max;
    }

    @Override
    public String getValueString() {
        return format.format(getValueRaw());
    }

    @Override
    public void setValueString(String s) {
        setValue(Double.parseDouble(s));
    }

    @Override
    public Object createButton(int x, int y, String name, Runnable init, Runnable save) {
        return new AbstractSliderButton(x, y, 150, 20, LiteralTextBuilder.literal(name + " : " + getValueString()), Mth.clamp((getValueRaw() - min) / (max - min), 0.0D, 1.0D)) {
            @Override
            protected void updateMessage() {
                setMessage(LiteralTextBuilder.literal(name + " : " + getValueString()));
            }

            @Override
            protected void applyValue() {
                DoubleClientOption.this.setValue(Mth.clamp(value * (max - min) + min, min, max));
                save.run();
            }
        };
    }

    @Override
    public Component createButtonText(String name) {
        return LiteralTextBuilder.literal(name + " : " + getValueString());
    }
}
