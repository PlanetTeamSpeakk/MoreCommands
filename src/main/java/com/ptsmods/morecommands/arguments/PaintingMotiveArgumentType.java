package com.ptsmods.morecommands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PaintingMotiveArgumentType implements CompatArgumentType<PaintingMotiveArgumentType, Identifier, ConstantSerialiser.ConstantProperties<PaintingMotiveArgumentType, Identifier>> {
	public static final ConstantSerialiser<PaintingMotiveArgumentType, Identifier> SERIALISER = new ConstantSerialiser<>(PaintingMotiveArgumentType::new);
	private static final DynamicCommandExceptionType MOTIVE_NOT_FOUND = new DynamicCommandExceptionType(o -> new LiteralMessage("Could not find a painting motive with an id of " + o + "."));

	private PaintingMotiveArgumentType() {}

	public static PaintingMotiveArgumentType paintingMotive() {
		return new PaintingMotiveArgumentType();
	}

	public static PaintingMotive getPaintingMotive(CommandContext<?> ctx, String argName) {
		return Registry.PAINTING_MOTIVE.get(ctx.getArgument(argName, Identifier.class));
	}

	@Override
	public Identifier parse(StringReader reader) throws CommandSyntaxException {
		Identifier id = Identifier.fromCommandInput(reader);
		if (!Registry.PAINTING_MOTIVE.containsId(id)) throw MOTIVE_NOT_FOUND.create(id);
		return id;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestFromIdentifier(Registry.PAINTING_MOTIVE.stream(), builder, Registry.PAINTING_MOTIVE::getId, motive -> new LiteralText(Registry.PAINTING_MOTIVE.getId(motive).getPath()));
	}

	@Override
	public Collection<String> getExamples() {
		return Lists.newArrayList("aztec", "fighters");
	}

	@Override
	public ArgumentType<Identifier> toVanillaArgumentType() {
		return IdentifierArgumentType.identifier();
	}

	@Override
	public ConstantSerialiser.ConstantProperties<PaintingMotiveArgumentType, Identifier> getProperties() {
		return SERIALISER.getProperties();
	}
}
