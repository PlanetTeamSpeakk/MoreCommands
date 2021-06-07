package com.ptsmods.morecommands.clientoption;

import com.google.common.collect.ImmutableList;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class ClientOption<T> {
	private static final List<ClientOption<?>> instances = new ArrayList<>();
	private T value;
	private final T defaultValue;
	private final BiConsumer<T, T> updateConsumer;
	private final List<String> comments;
	private boolean disabled = false;

	ClientOption(T defaultValue) {
		this(defaultValue, null, (String[]) null);
	}

	ClientOption(T defaultValue, BiConsumer<T, T> updateConsumer) {
		this(defaultValue, updateConsumer, (String[]) null);
	}

	ClientOption(T defaultValue, String... comments) {
		this(defaultValue, null, comments);
	}

	ClientOption(T defaultValue, BiConsumer<T, T> updateConsumer, String... comments) {
		this.value = this.defaultValue = defaultValue;
		this.updateConsumer = updateConsumer;
		this.comments = comments == null ? null : ImmutableList.copyOf(comments);
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
		T old = this.value;
		this.value = value;
		if (updateConsumer != null) updateConsumer.accept(old, value);
	}

	public abstract String getValueString();

	public abstract void setValueString(String s);

	public List<String> getComments() {
		return comments;
	}

	public abstract Object createButton(int x, int y, String name, Runnable init);

	public abstract Text createButtonText(String name);

	public void reset() {
		setValue(getDefaultValue());
	}

	public static void resetAll() {
		instances.forEach(ClientOption::reset);
	}
}
