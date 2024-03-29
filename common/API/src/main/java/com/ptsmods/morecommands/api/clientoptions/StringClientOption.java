package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.gui.PlaceholderEditBox;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@ExtensionMethod(ObjectExtensions.class)
public class StringClientOption extends ClientOption<String> {
    private final Predicate<String> predicate;

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue) {
        this(category, name, defaultValue, s -> true);
    }

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue, BiConsumer<String, String> updateConsumer) {
        this(category, name, defaultValue, updateConsumer, s -> true);
    }

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue, String... comments) {
        this(category, name, defaultValue, s -> true, comments);
    }

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue, BiConsumer<String, String> updateConsumer, String... comments) {
        this(category, name, defaultValue, updateConsumer, s -> true, comments);
    }

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue, Predicate<String> predicate) {
        super(category, name, defaultValue);
        this.predicate = predicate.or(s -> true);
    }

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue, BiConsumer<String, String> updateConsumer, Predicate<String> predicate) {
        super(category, name, defaultValue, updateConsumer);
        this.predicate = predicate.or(s -> true);
    }

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue, Predicate<String> predicate, String... comments) {
        super(category, name, defaultValue, comments);
        this.predicate = predicate.or(s -> true);
    }

    public StringClientOption(ClientOptionCategory category, String name, String defaultValue, BiConsumer<String, String> updateConsumer, Predicate<String> predicate, String... comments) {
        super(category, name, defaultValue, updateConsumer, comments);
        this.predicate = predicate.or(s -> true);
    }

    @Override
    public String getValueString() {
        return getValueRaw();
    }

    @Override
    public void setValueString(String s) {
        setValue(s);
    }

    @Override
    public Object createButton(Object screen, int x, int y, String name, Runnable init, Runnable save) {
        PlaceholderEditBox widget = new PlaceholderEditBox(Minecraft.getInstance().font, x, y, 150, 20, LiteralTextBuilder.literal(getName()));
        widget.setValue(getValueRaw());
        widget.setResponder(s -> {
            if (!predicate.test(s)) return;

            setValue(s);
            save.run();
        });
        return widget;
    }

    @Override
    public Component createButtonText(String name) {
        return LiteralTextBuilder.literal(getName());
    }
}
