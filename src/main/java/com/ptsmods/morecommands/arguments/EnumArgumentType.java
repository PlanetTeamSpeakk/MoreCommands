package com.ptsmods.morecommands.arguments;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumArgumentType implements CompatArgumentType<EnumArgumentType, String, EnumArgumentType.Properties> {
	public static final Serialiser SERIALISER = new Serialiser();
	private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(() -> "Could not find a value with the given name");
	private final Class<Enum<?>> clazz;
	private final Enum<?>[] values;
	private final List<String> strings;

	private EnumArgumentType(Class<Enum<?>> clazz, Enum<?>[] values, List<String> strings) {
		this.clazz = clazz;
		this.values = values;
		this.strings = strings;
	}

	public static <T extends Enum<T>> EnumArgumentType enumType(Class<T> clazz, T[] values) {
		return enumType(clazz, values, Enum::name);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> EnumArgumentType enumType(Class<T> clazz, T[] values, Function<T, String> stringMapper) {
		return new EnumArgumentType((Class<Enum<?>>) clazz, values, Arrays.stream(values).map(stringMapper).collect(Collectors.toList()));
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T getEnum(CommandContext<?> ctx, String argName) throws CommandSyntaxException {
		EnumArgumentType arg = ctx.getNodes().stream()
				.filter(node -> node.getNode().getName().equals(argName) && node.getNode() instanceof ArgumentCommandNode)
				.findAny()
				.map(node -> (EnumArgumentType) ((ArgumentCommandNode<?, String>) node.getNode()).getType())
				.orElseThrow(() -> new IllegalStateException("Enum argument not found"));

		String enumName = ctx.getArgument(argName, String.class);
		return (T) Arrays.stream(arg.values)
				.filter(e -> e.name().equals(enumName))
				.findAny()
				.orElseThrow(exc::create);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		String s = reader.readUnquotedString();
		for (int i = 0; i < strings.size() && i < values.length; i++)
			if (s.equalsIgnoreCase(strings.get(i)))
				return values[i].name();
		throw exc.createWithContext(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String read = builder.getRemaining().toLowerCase();
		for (String s : strings)
			if (s.toLowerCase().startsWith(read))
				builder.suggest(s);
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return strings;
	}

	@Override
	public ArgumentType<String> toVanillaArgumentType() {
		return StringArgumentType.word();
	}

	@Override
	public Properties getProperties() {
		return new Properties(clazz, values, strings);
	}

	public static class Serialiser implements ArgumentTypeSerialiser<EnumArgumentType, String, Properties> {
		private Serialiser() {}

		@SuppressWarnings("unchecked")
		@Override
		public EnumArgumentType fromPacket(PacketByteBuf buf) {
			try {
				Class<Enum<?>> clazz = (Class<Enum<?>>) Class.forName(buf.readString());
				int x = buf.readVarInt();
				List<Enum<?>> enums = new ArrayList<>();

				for (int i = 0; i < x; i++) {
					String s = buf.readString(); // Gotta retain the correct order, so we use a nested for loop instead or first reading all strings and checking for each Enum if it's in the list. :)
					for (Enum<?> e : clazz.getEnumConstants())
						if (e.name().equals(s))
							enums.add(e);
				}

				List<String> strings = new ArrayList<>();
				x = buf.readVarInt();

				for (int i = 0; i < x; i++)
					strings.add(buf.readString());

				return new EnumArgumentType(clazz, enums.toArray(new Enum[0]), strings);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e); // Should not happen.
			}
		}

		@Override
		public void writeJson(Properties properties, JsonObject json) {
			json.addProperty("class", properties.clazz.getName());

			JsonArray a = new JsonArray();
			for (Enum<?> e : properties.values)
				a.add(e.name());

			json.add("values", a);

			JsonArray b = new JsonArray();
			properties.strings.forEach(b::add);
			json.add("strings", b);
		}
	}

	public static class Properties implements ArgumentTypeProperties<EnumArgumentType, String, Properties> {
		private final Class<Enum<?>> clazz;
		private final Enum<?>[] values;
		private final List<String> strings;

		public Properties(Class<Enum<?>> clazz, Enum<?>[] values, List<String> strings) {
			this.clazz = clazz;
			this.values = values;
			this.strings = strings;
		}

		@Override
		public EnumArgumentType createType() {
			return new EnumArgumentType(clazz, values, strings);
		}

		@Override
		public ArgumentTypeSerialiser<EnumArgumentType, String, Properties> getSerialiser() {
			return SERIALISER;
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeString(clazz.getName());

			buf.writeVarInt(values.length);
			for (Enum<?> e : values)
				buf.writeString(e.name());

			buf.writeVarInt(strings.size());
			strings.forEach(buf::writeString);
		}
	}
}
