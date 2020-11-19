package com.ptsmods.morecommands.arguments;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {

    private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(() -> "Could not find a value with the given name");
    private final Class<T> clazz;
    private final T[] values;
    private final List<String> strings;

    private EnumArgumentType(Class<T> clazz, T[] values, List<String> strings) {
        this.clazz = clazz;
        this.values = values;
        this.strings = strings;
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumType(Class<T> clazz, T[] values) {
        return enumType(clazz, values, Enum::name);
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumType(Class<T> clazz, T[] values, Function<T, String> toStringFunction) {
        List<String> strings = new ArrayList<>();
        for (T e : values)
            strings.add(toStringFunction.apply(e));
        return new EnumArgumentType<>(clazz, values, strings);
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        for (int i = 0; i < strings.size() && i < values.length; i++)
            if (s.equalsIgnoreCase(strings.get(i)))
                return (T) values[i];
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

    public static class Serialiser<T extends Enum<T>> implements ArgumentSerializer<EnumArgumentType<T>> {

        @Override
        public void toPacket(EnumArgumentType<T> arg, PacketByteBuf buf) {
            buf.writeString(arg.clazz.getName());
            buf.writeVarInt(arg.values.length);
            for (Enum<?> e : arg.values)
                buf.writeString(e.name());
            buf.writeVarInt(arg.strings.size());
            arg.strings.forEach(buf::writeString);
        }

        @Override
        public EnumArgumentType<T> fromPacket(PacketByteBuf buf) {
            try {
                Class<Enum<?>> clazz = (Class<Enum<?>>) Class.forName(buf.readString());
                int x = buf.readVarInt();
                List<Enum<?>> enums = new ArrayList<>();
                for (int i = 0; i < x; i++) {
                    String s = buf.readString(); // Gotta retain the correct order so we use a nested for loop instead or first reading all strings and checking for each Enum if it's in the list. :)
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
        public void toJson(EnumArgumentType<T> arg, JsonObject json) {
            json.addProperty("class", arg.clazz.getName());
            JsonArray a = new JsonArray();
            for (Enum<?> e : arg.values)
                a.add(e.name());
            json.add("values", a);
            JsonArray b = new JsonArray();
            arg.strings.forEach(b::add);
            json.add("strings", b);
        }
    }

}
