package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class IntegerClientOption extends ClientOption<Integer> {
    private final int min, max;

    public IntegerClientOption(ClientOptionCategory category, String name, int defaultValue, int min, int max) {
        super(category, name, defaultValue);
        this.min = min;
        this.max = max;
    }

    public IntegerClientOption(ClientOptionCategory category, String name, int defaultValue, int min, int max, BiConsumer<Integer, Integer> updateConsumer) {
        super(category, name, defaultValue, updateConsumer);
        this.min = min;
        this.max = max;
    }

    public IntegerClientOption(ClientOptionCategory category, String name, int defaultValue, int min, int max, String... comments) {
        super(category, name, defaultValue, comments);
        this.min = min;
        this.max = max;
    }

    public IntegerClientOption(ClientOptionCategory category, String name, int defaultValue, int min, int max, BiConsumer<Integer, Integer> updateConsumer, String... comments) {
        super(category, name, defaultValue, updateConsumer, comments);
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
    public Object createButton(int x, int y, String name, Runnable init, Runnable save) {
        return new AbstractSliderButton(x, y, 150, 20, LiteralTextBuilder.literal(name + " : " + getValueRaw()), Mth.clamp((double) (getValueRaw() - min) / (double) (max - min), 0.0D, 1.0D)) {
            @Override
            protected void updateMessage() {
                setMessage(LiteralTextBuilder.literal(name + " : " + getValueRaw()));
            }

            @Override
            protected void applyValue() {
                IntegerClientOption.this.setValue((int) Mth.clamp(value * (max - min) + min, min, max));
                save.run();
            }
        };
    }

    @Override
    public Component createButtonText(String name) {
        return LiteralTextBuilder.literal(name + " : " + getValueRaw());
    }
}
