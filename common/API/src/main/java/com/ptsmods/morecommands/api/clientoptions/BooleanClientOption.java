package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public class BooleanClientOption extends ClientOption<Boolean> {
    public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue) {
        super(category, name, defaultValue);
    }

    public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue, BiConsumer<Boolean, Boolean> updateConsumer) {
        super(category, name, defaultValue, updateConsumer);
    }

    public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue, String... comments) {
        super(category, name, defaultValue, comments);
    }

    public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue, BiConsumer<Boolean, Boolean> updateConsumer, String... comments) {
        super(category, name, defaultValue, updateConsumer, comments);
    }

    @Override
    public String getValueString() {
        return String.valueOf(getValueRaw());
    }

    @Override
    public void setValueString(String s) {
        setValue("true".equals(s));
    }

    @Override
    public Object createButton(Object screen, int x, int y, String name, Runnable init, Runnable save) {
        return ClientCompat.get().newButton((Screen) screen, x, y, 150, 20, createButtonText(name), btn -> {
            setValue(!getValueRaw());
            btn.setMessage(createButtonText(name));
            init.run();
            save.run();
        }, null);
    }

    @Override
    public Component createButtonText(String name) {
        return LiteralTextBuilder.literal(name + " : " + Util.formatFromBool(getValueRaw()) + String.valueOf(getValueRaw()).toUpperCase());
    }
}
