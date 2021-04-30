package com.ptsmods.morecommands.clientoption;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientOption<T> {
	private static final List<ClientOption<?>> instances = new ArrayList<>();
	private T value;
	private final T defaultValue;
	private boolean disabled = false;

	ClientOption(T defaultValue) {
		this.value = this.defaultValue = defaultValue;
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

	public abstract Class<T> getType();

	public void reset() {
		setValue(getDefaultValue());
	}

	public static void resetAll() {
		instances.forEach(ClientOption::reset);
	}
}
