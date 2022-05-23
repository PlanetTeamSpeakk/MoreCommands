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
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "potion")
public class PotionArgumentType implements CompatArgumentType<PotionArgumentType, Identifier, ConstantSerialiser.ConstantProperties<PotionArgumentType, Identifier>> {
    public static final ConstantSerialiser<PotionArgumentType, Identifier> SERIALISER = new ConstantSerialiser<>(PotionArgumentType::new);
    private static final DynamicCommandExceptionType POTION_NOT_FOUND = new DynamicCommandExceptionType(o -> new LiteralMessage("Could not find a potion with an id of " + o + "."));

    public static Potion getPotion(CommandContext<?> ctx, String argName) {
        return Registry.POTION.get(ctx.getArgument(argName, Identifier.class));
    }

    @Override
    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.fromCommandInput(reader);
        if (!Registry.POTION.containsId(id)) throw POTION_NOT_FOUND.create(id);
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestFromIdentifier(Registry.POTION.stream(), builder, Registry.POTION::getId, potion -> LiteralTextBuilder.builder(Registry.POTION.getId(potion).getPath()).build());
    }

    @Override
    public Collection<String> getExamples() {
        return Lists.newArrayList("weakness", "long_strength");
    }

    @Override
    public ArgumentType<Identifier> toVanillaArgumentType() {
        return IdentifierArgumentType.identifier();
    }

    @Override
    public ConstantSerialiser.ConstantProperties<PotionArgumentType, Identifier> getProperties() {
        return SERIALISER.getProperties();
    }
}
