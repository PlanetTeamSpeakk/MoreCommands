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
import net.minecraft.potion.Potion;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PotionArgumentType implements ArgumentType<Potion>, ServerSideArgumentType {
    private static final DynamicCommandExceptionType POTION_NOT_FOUND = new DynamicCommandExceptionType(o -> new LiteralMessage("Could not find a potion with an id of " + o + "."));

    @Override
    public Potion parse(StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.fromCommandInput(reader);
        return Registry.POTION.getOrEmpty(id).orElseThrow(() -> POTION_NOT_FOUND.create(id));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestFromIdentifier(Registry.POTION.stream(), builder, Registry.POTION::getId, potion -> new LiteralText(Registry.POTION.getId(potion).getPath()));
    }

    @Override
    public Collection<String> getExamples() {
        return Lists.newArrayList("weakness", "long_strength");
    }

    @Override
    public ArgumentType<?> toVanillaArgumentType() {
        return IdentifierArgumentType.identifier();
    }
}
