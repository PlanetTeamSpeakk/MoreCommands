package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RegistryArgumentType<T> implements ArgumentType<T> {

    private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(() -> "Could not find the given key in the registry");
    private final Registry<T> registry;
    private final Collection<Identifier> ids;
    private final List<String> examples;

    public RegistryArgumentType(Registry<T> registry) {
        this(registry, genExamples(registry));
    }

    public RegistryArgumentType(Registry<T> registry, List<String> examples) {
        this(registry, examples, registry.getIds());
    }

    private RegistryArgumentType(Registry<T> registry, List<String> examples, Collection<Identifier> ids) {
        this.registry = registry;
        this.ids = ids;
        this.examples = ImmutableList.copyOf(examples);
    }

    private static <T> List<String> genExamples(Registry<T> registry) {
        List<String> examples = new ArrayList<>();
        int size = registry.getIds().size();
        Random random = new Random();
        if (size <= 3)
            for (Identifier id : registry.getIds())
                examples.add(id.toString());
        else
            for (int i = 0; i < 3; i++) {
                T item = registry.get(random.nextInt(size));
                if (item != null && registry.getId(item) != null) examples.add(registry.getId(item).toString());
            }
        return examples;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String s = MoreCommands.readTillSpaceOrEnd(reader).toLowerCase();
        for (Identifier id : ids)
            if (id.getPath().equalsIgnoreCase(s) || id.toString().equalsIgnoreCase(s)) return registry.get(id);
        throw exc.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String s = builder.getRemaining().toLowerCase();
        for (Identifier id : ids)
            if (id.getPath().startsWith(s) || id.toString().startsWith(s)) builder.suggest(id.toString());
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return examples;
    }

    public static class Serialiser<T> implements ArgumentSerializer<RegistryArgumentType<T>> {

        @Override
        public void toPacket(RegistryArgumentType<T> arg, PacketByteBuf buf) {
            buf.writeIdentifier(MoreCommands.getKey((MutableRegistry<T>) arg.registry).getValue());
            buf.writeVarInt(arg.examples.size());
            arg.examples.forEach(buf::writeString);
            buf.writeVarInt(arg.ids.size());
            arg.ids.forEach(buf::writeIdentifier);
        }

        @Override
        public RegistryArgumentType<T> fromPacket(PacketByteBuf buf) {
            Identifier registryId = buf.readIdentifier();
            List<String> examples = new ArrayList<>();
            int x = buf.readVarInt();
            for (int i = 0; i < x; i++)
                examples.add(buf.readString());
            List<Identifier> ids = new ArrayList<>();
            x = buf.readVarInt();
            for (int i = 0; i < x; i++)
                ids.add(buf.readIdentifier());
            return new RegistryArgumentType(Objects.requireNonNull(MoreCommands.getRegistry(RegistryKey.ofRegistry(registryId))), ids);
        }

        @Override
        public void toJson(RegistryArgumentType<T> arg, JsonObject json) {
            json.addProperty("registry", MoreCommands.getKey((MutableRegistry<T>) arg.registry).getValue().toString());
            JsonArray a = new JsonArray();
            arg.examples.forEach(a::add);
            json.add("examples", a);
            JsonArray b = new JsonArray();
            arg.ids.forEach(id -> b.add(id.toString()));
            json.add("ids", b);
        }
    }

}
