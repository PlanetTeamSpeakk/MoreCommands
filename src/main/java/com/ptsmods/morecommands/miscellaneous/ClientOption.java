package com.ptsmods.morecommands.miscellaneous;

import java.util.ArrayList;
import java.util.List;

public class ClientOption<T> {
    private static final List<ClientOption<?>> instances = new ArrayList<>();
    private T value;
    private final T defaultValue;
    private boolean disabled = false;
    private final Class<T> type;

    ClientOption(T defaultValue) {
        this.value = this.defaultValue = defaultValue;
        type = ReflectionHelper.cast(value instanceof Boolean ? Boolean.class : value instanceof Integer ? Integer.class : null);
        instances.add(this);
    }

    public void setDisabled(boolean b) {
        disabled = b;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return disabled ? defaultValue : value;
    }

    public T getValueRaw() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    public void reset() {
        setValue(getDefaultValue());
    }

    public static void resetAll() {
        instances.forEach(ClientOption::reset);
    }
}
