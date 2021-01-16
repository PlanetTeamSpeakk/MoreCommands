package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LimitedStringArgumentType implements ArgumentType<String> {

    private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(() -> "The given value was invalid.");
    private final StringArgumentType parent;
    private final Collection<String> possibilities;

    protected LimitedStringArgumentType(StringArgumentType.StringType type, Collection<String> possibilities) {
        parent = type == StringArgumentType.StringType.SINGLE_WORD ? StringArgumentType.word() :
                type == StringArgumentType.StringType.QUOTABLE_PHRASE ? StringArgumentType.string() :
                        StringArgumentType.greedyString();
        this.possibilities = possibilities;
        while (possibilities.contains(null))
            possibilities.remove(null);
    }

    public static LimitedStringArgumentType word(Collection<String> possibilities) {
        return new LimitedStringArgumentType(StringArgumentType.StringType.SINGLE_WORD, possibilities);
    }

    public static LimitedStringArgumentType string(Collection<String> possibilities) {
        return new LimitedStringArgumentType(StringArgumentType.StringType.QUOTABLE_PHRASE, possibilities);
    }

    public static LimitedStringArgumentType greedyString(Collection<String> possibilities) {
        return new LimitedStringArgumentType(StringArgumentType.StringType.GREEDY_PHRASE, possibilities);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String s = parent.parse(reader);
        for (String s0 : possibilities)
            if (s.equalsIgnoreCase(s0)) return s0;
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

    public static class Serialiser implements ArgumentSerializer<LimitedStringArgumentType> {

        @Override
        public void toPacket(LimitedStringArgumentType arg, PacketByteBuf buf) {
            buf.writeByte(arg.parent.getType().ordinal());
            buf.writeVarInt(arg.possibilities.size());
            arg.possibilities.forEach(buf::writeString);
        }

        @Override
        public LimitedStringArgumentType fromPacket(PacketByteBuf buf) {
            StringArgumentType.StringType type = StringArgumentType.StringType.values()[buf.readByte()];
            List<String> possibilities = new ArrayList<>();
            int x = buf.readVarInt();
            for (int i = 0; i < x; i++)
                possibilities.add(buf.readString());
            return new LimitedStringArgumentType(type, possibilities);
        }

        @Override
        public void toJson(LimitedStringArgumentType arg, JsonObject json) {
            json.addProperty("type", arg.parent.getType().name());
            JsonArray a = new JsonArray();
            arg.possibilities.forEach(a::add);
            json.add("possibilities", a);
        }
    }

}
