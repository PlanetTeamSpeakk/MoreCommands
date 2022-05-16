package com.ptsmods.morecommands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "paintingVariant")
public class PaintingVariantArgumentType implements CompatArgumentType<PaintingVariantArgumentType, Identifier, ConstantSerialiser.ConstantProperties<PaintingVariantArgumentType, Identifier>> {
	public static final ConstantSerialiser<PaintingVariantArgumentType, Identifier> SERIALISER = new ConstantSerialiser<>(PaintingVariantArgumentType::new);
	private static final DynamicCommandExceptionType MOTIVE_NOT_FOUND = new DynamicCommandExceptionType(o -> LiteralTextBuilder.builder("Could not find a painting motive with an id of " + o + ".").build());

	public static PaintingVariant getPaintingVariant(CommandContext<?> ctx, String argName) {
		return Registry.PAINTING_VARIANT.get(ctx.getArgument(argName, Identifier.class));
	}

	@Override
	public Identifier parse(StringReader reader) throws CommandSyntaxException {
		Identifier id = Identifier.fromCommandInput(reader);
		if (!Registry.PAINTING_VARIANT.containsId(id)) throw MOTIVE_NOT_FOUND.create(id);
		return id;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestFromIdentifier(Registry.PAINTING_VARIANT.stream(), builder, Registry.PAINTING_VARIANT::getId, variant -> LiteralTextBuilder.builder(Registry.PAINTING_VARIANT.getId(variant).getPath()).build());
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
	public ConstantSerialiser.ConstantProperties<PaintingVariantArgumentType, Identifier> getProperties() {
		return SERIALISER.getProperties();
	}
}
