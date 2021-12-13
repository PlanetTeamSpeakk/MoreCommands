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
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PaintingMotiveArgumentType implements ArgumentType<PaintingMotive>, ServerSideArgumentType {
    private static final DynamicCommandExceptionType MOTIVE_NOT_FOUND = new DynamicCommandExceptionType(o -> new LiteralMessage("Could not find a painting motive with an id of " + o + "."));

    @Override
    public PaintingMotive parse(StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.fromCommandInput(reader);
        return Registry.PAINTING_MOTIVE.getOrEmpty(id).orElseThrow(() -> MOTIVE_NOT_FOUND.create(id));
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
    public ArgumentType<?> toVanillaArgumentType() {
        return IdentifierArgumentType.identifier();
    }
}
