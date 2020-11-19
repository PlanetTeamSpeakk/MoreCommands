package com.ptsmods.morecommands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class CrampedStringArgumentType implements ArgumentType<String> {

    private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(() -> "The given string exceeds the maximum length");
    private final StringArgumentType parent;
    private final int minLength, maxLength;

    public CrampedStringArgumentType(StringArgumentType parent, int minLength, int maxLength) {
        this.parent = parent;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String s = parent.parse(reader);
        if (s.length() > maxLength || s.length() < minLength) throw exc.createWithContext(reader);
        else return s;
    }

    public static class Serialiser implements ArgumentSerializer<CrampedStringArgumentType> {

        @Override
        public void toPacket(CrampedStringArgumentType arg, PacketByteBuf buf) {
            buf.writeByte(arg.parent.getType().ordinal());
            buf.writeVarInt(arg.minLength);
            buf.writeVarInt(arg.maxLength);
        }

        @Override
        public CrampedStringArgumentType fromPacket(PacketByteBuf buf) {
            StringArgumentType.StringType type = StringArgumentType.StringType.values()[buf.readByte()];
            return new CrampedStringArgumentType(type == StringArgumentType.StringType.SINGLE_WORD ? StringArgumentType.word() : type == StringArgumentType.StringType.QUOTABLE_PHRASE ? StringArgumentType.string() : StringArgumentType.greedyString(), buf.readVarInt(), buf.readVarInt());
        }

        @Override
        public void toJson(CrampedStringArgumentType arg, JsonObject json) {
            json.addProperty("type", arg.parent.getType().name());
            json.addProperty("minLength", arg.minLength);
            json.addProperty("maxLength", arg.maxLength);
        }
    }

}
