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
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "potion")
public class PotionArgumentType implements CompatArgumentType<PotionArgumentType, ResourceLocation, ConstantSerialiser.ConstantProperties<PotionArgumentType, ResourceLocation>> {
    public static final ConstantSerialiser<PotionArgumentType, ResourceLocation> SERIALISER = new ConstantSerialiser<>(PotionArgumentType::new);
    private static final DynamicCommandExceptionType POTION_NOT_FOUND = new DynamicCommandExceptionType(o -> new LiteralMessage("Could not find a potion with an id of " + o + "."));

    public static Potion getPotion(CommandContext<?> ctx, String argName) {
        return Registry.POTION.get(ctx.getArgument(argName, ResourceLocation.class));
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        if (!Registry.POTION.containsKey(id)) throw POTION_NOT_FOUND.create(id);
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(Registry.POTION.stream(), builder, Registry.POTION::getKey, potion -> LiteralTextBuilder.literal(Registry.POTION.getKey(potion).getPath()));
    }

    @Override
    public Collection<String> getExamples() {
        return Lists.newArrayList("weakness", "long_strength");
    }

    @Override
    public ArgumentType<ResourceLocation> toVanillaArgumentType() {
        return ResourceLocationArgument.id();
    }

    @Override
    public ConstantSerialiser.ConstantProperties<PotionArgumentType, ResourceLocation> getProperties() {
        return SERIALISER.getProperties();
    }
}
