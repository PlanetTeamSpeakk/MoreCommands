package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;
import java.util.function.BiConsumer;

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
        return new SliderWidget(x, y, 150, 20, LiteralTextBuilder.literal(name + " : " + getValueString()), MathHelper.clamp((getValueRaw() - min) / (max - min), 0.0D, 1.0D)) {
            @Override
            protected void updateMessage() {
                setMessage(LiteralTextBuilder.literal(name + " : " + getValueString()));
            }

            @Override
            protected void applyValue() {
                DoubleClientOption.this.setValue(MathHelper.clamp(value * (max - min) + min, min, max));
                save.run();
            }
        };
    }

    @Override
    public Text createButtonText(String name) {
        return LiteralTextBuilder.literal(name + " : " + getValueString());
    }
}
