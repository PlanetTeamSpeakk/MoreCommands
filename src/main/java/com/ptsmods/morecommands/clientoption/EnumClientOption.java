package com.ptsmods.morecommands.clientoption;

import com.ptsmods.morecommands.miscellaneous.FormattingColour;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class EnumClientOption<T extends Enum<T>> extends ClientOption<T> {
    private final Class<T> type;

    EnumClientOption(Class<T> type, T defaultValue) {
        super(defaultValue);
        this.type = type;
    }

    EnumClientOption(Class<T> type, T defaultValue, BiConsumer<T, T> updateConsumer) {
        super(defaultValue, updateConsumer);
        this.type = type;
    }

    EnumClientOption(Class<T> type, T defaultValue, String... comments) {
        super(defaultValue, comments);
        this.type = type;
    }

    EnumClientOption(Class<T> type, T defaultValue, BiConsumer<T, T> updateConsumer, String... comments) {
        super(defaultValue, updateConsumer, comments);
        this.type = type;
    }

    @Override
    public String getValueString() {
        return getValue() == null ? null : getValue().name();
    }

    @Override
    public void setValueString(String s) {
        setValue(s == null || "null".equals(s) ? null : Arrays.stream(type.getEnumConstants()).filter(value -> value.name().equals(s)).findFirst().orElse(getDefaultValue()));
    }

    @Override
    public Object createButton(int x, int y, String name, Runnable init) {
        AtomicInteger btn = new AtomicInteger();
        return new ButtonWidget(x, y, 150, 20, createButtonText(name), button -> {
            T[] values = type.getEnumConstants();
            int index = (getValue() == null ? btn.get() == 0 ? -1 : 1 : getValue().ordinal()) + (btn.get() == 0 ? 1 : -1);
            setValue(values[(index + (index < 0 ? values.length : 0)) % values.length]);
            button.setMessage(createButtonText(name));
            ClientOptions.write();
        }) {
            @Override
            protected boolean isValidClickButton(int button) {
                btn.set(button);
                return button == 0 || button == 1;
            }
        };
    }

    @Override
    public Text createButtonText(String name) {
        return new LiteralText(name + " : " + (type == Formatting.class ? getValue() : type == FormattingColour.class ? ((FormattingColour) getValue()).asFormatting() : "") + getValue().name());
    }
}
