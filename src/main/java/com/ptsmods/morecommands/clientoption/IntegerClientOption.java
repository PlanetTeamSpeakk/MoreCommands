package com.ptsmods.morecommands.clientoption;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;

public class IntegerClientOption extends ClientOption<Integer> {
    private final int min, max;

    IntegerClientOption(Integer defaultValue, int min, int max) {
        super(defaultValue);
        this.min = min;
        this.max = max;
    }

    IntegerClientOption(Integer defaultValue, BiConsumer<Integer, Integer> updateConsumer, int min, int max) {
        super(defaultValue, updateConsumer);
        this.min = min;
        this.max = max;
    }

    IntegerClientOption(Integer defaultValue, int min, int max, String... comments) {
        super(defaultValue, comments);
        this.min = min;
        this.max = max;
    }

    IntegerClientOption(Integer defaultValue, BiConsumer<Integer, Integer> updateConsumer, int min, int max, String... comments) {
        super(defaultValue, updateConsumer, comments);
        this.min = min;
        this.max = max;
    }

    @Override
    public String getValueString() {
        return String.valueOf(getValueRaw());
    }

    @Override
    public void setValueString(String s) {
        setValue(Integer.parseInt(s));
    }

    @Override
    public Object createButton(int x, int y, String name, Runnable init) {
        return new SliderWidget(x, y, 150, 20, new LiteralText(name + " : " + getValueRaw()), MathHelper.clamp((double) (getValueRaw() - min) / (double) (max - min), 0.0D, 1.0D)) {
            @Override
            protected void updateMessage() {
                setMessage(new LiteralText(name + " : " + getValueRaw()));
            }

            @Override
            protected void applyValue() {
                IntegerClientOption.this.setValue((int) MathHelper.clamp(value * (max - min) + min, min, max));
                ClientOptions.write();
            }
        };
    }

    @Override
    public Text createButtonText(String name) {
        return new LiteralText(name + " : " + getValueRaw());
    }
}
