package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.miscellaneous.FormattingColour;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class EnumClientOption<T extends Enum<T>> extends ClientOption<T> {
    private final Class<T> type;

    public EnumClientOption(ClientOptionCategory category, String name, Class<T> type, T defaultValue) {
        super(category, name, defaultValue);
        this.type = type;
    }

    public EnumClientOption(ClientOptionCategory category, String name, Class<T> type, T defaultValue, BiConsumer<T, T> updateConsumer) {
        super(category, name, defaultValue, updateConsumer);
        this.type = type;
    }

    public EnumClientOption(ClientOptionCategory category, String name, Class<T> type, T defaultValue, String... comments) {
        super(category, name, defaultValue, comments);
        this.type = type;
    }

    public EnumClientOption(ClientOptionCategory category, String name, Class<T> type, T defaultValue, BiConsumer<T, T> updateConsumer, String... comments) {
        super(category, name, defaultValue, updateConsumer, comments);
        this.type = type;
    }

    @Override
    public String getValueString() {
        return getValue() == null ? null : getValue().name();
    }

    @Override
    public void setValueString(String s) {
        setValue(s == null || "null".equals(s) ? null : Arrays.stream(type.getEnumConstants())
                .filter(value -> value.name().equals(s))
                .findFirst()
                .orElse(getDefaultValue()));
    }

    @Override
    public Object createButton(int x, int y, String name, Runnable init, Runnable save) {
        AtomicInteger btn = new AtomicInteger();
        return new Button(x, y, 150, 20, createButtonText(name), button -> {
            T[] values = type.getEnumConstants();
            int index = (getValue() == null ? btn.get() == 0 ? -1 : 1 : getValue().ordinal()) + (btn.get() == 0 ? 1 : -1);
            setValue(values[(index + (index < 0 ? values.length : 0)) % values.length]);
            button.setMessage(createButtonText(name));
            save.run();
        }) {
            @Override
            protected boolean isValidClickButton(int button) {
                btn.set(button);
                return button == 0 || button == 1;
            }
        };
    }

    @Override
    public Component createButtonText(String name) {
        return LiteralTextBuilder.builder(name + " : " + (type == ChatFormatting.class ? getValue() : type == FormattingColour.class ?
                ((FormattingColour) getValue()).asFormatting() : "") + getValue().name()).build();
    }
}
