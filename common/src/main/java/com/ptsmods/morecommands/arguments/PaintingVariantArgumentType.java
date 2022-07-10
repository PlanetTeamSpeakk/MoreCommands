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
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "paintingVariant")
public class PaintingVariantArgumentType implements CompatArgumentType<PaintingVariantArgumentType, ResourceLocation, ConstantSerialiser.ConstantProperties<PaintingVariantArgumentType, ResourceLocation>> {
    public static final ConstantSerialiser<PaintingVariantArgumentType, ResourceLocation> SERIALISER = new ConstantSerialiser<>(PaintingVariantArgumentType::new);
    private static final DynamicCommandExceptionType MOTIVE_NOT_FOUND = new DynamicCommandExceptionType(o -> LiteralTextBuilder.literal("Could not find a painting motive with an id of " + o + "."));

    public static PaintingVariant getPaintingVariant(CommandContext<?> ctx, String argName) {
        return Registry.PAINTING_VARIANT.get(ctx.getArgument(argName, ResourceLocation.class));
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        if (!Registry.PAINTING_VARIANT.containsKey(id)) throw MOTIVE_NOT_FOUND.create(id);
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(Registry.PAINTING_VARIANT.stream(), builder, Registry.PAINTING_VARIANT::getKey, variant -> LiteralTextBuilder.literal(Registry.PAINTING_VARIANT.getKey(variant).getPath()));
    }

    @Override
    public Collection<String> getExamples() {
        return Lists.newArrayList("aztec", "fighters");
    }

    @Override
    public ArgumentType<ResourceLocation> toVanillaArgumentType() {
        return ResourceLocationArgument.id();
    }

    @Override
    public ConstantSerialiser.ConstantProperties<PaintingVariantArgumentType, ResourceLocation> getProperties() {
        return SERIALISER.getProperties();
    }
}
