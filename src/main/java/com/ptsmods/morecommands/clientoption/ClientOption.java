package com.ptsmods.morecommands.clientoption;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class ClientOption<T> {
	private static final Map<ClientOptionCategory, Map<String, ClientOption<?>>> options = new LinkedHashMap<>();
	private final ClientOptionCategory category;
	private final String name;
	private T value;
	private final T defaultValue;
	private final BiConsumer<T, T> updateConsumer;
	private final List<String> comments;
	private boolean disabled = false;

	ClientOption(ClientOptionCategory category, String name, T defaultValue) {
		this(category, name, defaultValue, null, (String[]) null);
	}

	ClientOption(ClientOptionCategory category, String name, T defaultValue, BiConsumer<T, T> updateConsumer) {
		this(category, name, defaultValue, updateConsumer, (String[]) null);
	}

	ClientOption(ClientOptionCategory category, String name, T defaultValue, String... comments) {
		this(category, name, defaultValue, null, comments);
	}

	ClientOption(ClientOptionCategory category, String name, T defaultValue, BiConsumer<T, T> updateConsumer, String... comments) {
		this.category = category;
		this.name = name;
		this.value = this.defaultValue = defaultValue;
		this.updateConsumer = updateConsumer;
		this.comments = comments == null ? null : ImmutableList.copyOf(comments);

		options.computeIfAbsent(category, t -> new LinkedHashMap<>()).put(name, this);
	}

	public ClientOptionCategory getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		String key = name.replace(" ", "");
		return key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1);
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
		options.values().forEach(m -> m.values().forEach(ClientOption::reset));
	}

	public static Map<ClientOptionCategory, Map<String, ClientOption<?>>> getOptions() {
		return options.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ImmutableMap.copyOf(entry.getValue())));
	}

	public static Map<String, ClientOption<?>> getUnmappedOptions() {
		return getOptions().values().stream()
				.flatMap(m -> m.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, LinkedHashMap::new));
	}
}
