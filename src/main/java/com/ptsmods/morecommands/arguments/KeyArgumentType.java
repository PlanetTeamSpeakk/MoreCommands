package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class KeyArgumentType implements CompatArgumentType<KeyArgumentType, String, ConstantSerialiser.ConstantProperties<KeyArgumentType, String>> {
	public static final ConstantSerialiser<KeyArgumentType, String> SERIALISER = new ConstantSerialiser<>(KeyArgumentType::new);
	private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(() -> "The given value was invalid.");
	private final StringArgumentType parent = StringArgumentType.word();
	private final Collection<String> possibilities = MoreCommandsClient.getKeys();

	private KeyArgumentType() {}

	public static KeyArgumentType key() {
		return new KeyArgumentType();
	}

	public static int getKey(CommandContext<?> ctx, String argName) {
		return Integer.parseInt(ctx.getArgument(argName, String.class));
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		String s = parent.parse(reader);
		for (String s0 : possibilities)
			if (s.equalsIgnoreCase(s0)) return "" + MoreCommandsClient.getKeyCodeForKey(s0);
		throw exc.createWithContext(reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String s = builder.getRemaining().toLowerCase();
		for (String entry : possibilities)
			if (entry.toLowerCase().startsWith(s)) builder.suggest(entry);
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return ImmutableList.copyOf(possibilities);
	}

	@Override
	public ArgumentType<String> toVanillaArgumentType() {
		return StringArgumentType.word();
	}

	@Override
	public ConstantSerialiser.ConstantProperties<KeyArgumentType, String> getProperties() {
		return SERIALISER.getProperties();
	}
}
