package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.addons.AbstractWidgetAddon;
import com.ptsmods.morecommands.api.miscellaneous.FormattingColour;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.function.BiConsumer;

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
    public Object createButton(Object screen, int x, int y, String name, Runnable init, Runnable save) {
        Button btn = ClientCompat.get().newButton((Screen) screen, x, y, 150, 20, createButtonText(name), button -> {
            T[] values = type.getEnumConstants();
            int mouseBtn = ((AbstractWidgetAddon) button).getLastMouseButton();
            int index = (getValue() == null ? mouseBtn == 0 ? -1 : 1 : getValue().ordinal()) + (mouseBtn == 0 ? 1 : -1);
            setValue(values[(index + (index < 0 ? values.length : 0)) % values.length]);
            button.setMessage(createButtonText(name));
            save.run();
        }, null);

        ((AbstractWidgetAddon) btn).setValidButtons(0, 1);
        return btn;
    }

    @Override
    public Component createButtonText(String name) {
        return LiteralTextBuilder.builder(name + " : " + (type == ChatFormatting.class ? getValue() : type == FormattingColour.class ?
                ((FormattingColour) getValue()).asFormatting() : "") + getValue().name()).build();
    }
}
