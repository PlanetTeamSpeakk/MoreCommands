package com.ptsmods.morecommands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketByteBuf;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CrampedStringArgumentType implements CompatArgumentType<CrampedStringArgumentType, String, CrampedStringArgumentType.Properties> {
	public static final Serialiser SERIALISER = new Serialiser();
	private static final SimpleCommandExceptionType excMax = new SimpleCommandExceptionType(() -> "The given string exceeds the maximum length");
	private static final SimpleCommandExceptionType excMin = new SimpleCommandExceptionType(() -> "The given string exceeds the minimum length");
	private final StringArgumentType parent;
	private final int minLength, maxLength;

	public static CrampedStringArgumentType crampedWord(int minLength, int maxLength) {
		return new CrampedStringArgumentType(StringArgumentType.word(), minLength, maxLength);
	}

	public static CrampedStringArgumentType crampedString(int minLength, int maxLength) {
		return new CrampedStringArgumentType(StringArgumentType.string(), minLength, maxLength);
	}

	public static CrampedStringArgumentType crampedGreedyString(int minLength, int maxLength) {
		return new CrampedStringArgumentType(StringArgumentType.greedyString(), minLength, maxLength);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		String s = parent.parse(reader);
		if (s.length() > maxLength) throw excMax.createWithContext(reader);
		if (s.length() < minLength) throw excMin.createWithContext(reader);
		else return s;
	}

	@Override
	public ArgumentType<String> toVanillaArgumentType() {
		return parent;
	}

	@Override
	public Properties getProperties() {
		return new Properties(parent.getType(), minLength, maxLength);
	}

	public static class Serialiser implements ArgumentTypeSerialiser<CrampedStringArgumentType, String, Properties> {
		private Serialiser() {}

		@Override
		public CrampedStringArgumentType fromPacket(PacketByteBuf buf) {
			StringArgumentType.StringType type = StringArgumentType.StringType.values()[buf.readByte()];
			return new CrampedStringArgumentType(type == StringArgumentType.StringType.SINGLE_WORD ? StringArgumentType.word() :
					type == StringArgumentType.StringType.QUOTABLE_PHRASE ? StringArgumentType.string() : StringArgumentType.greedyString(), buf.readVarInt(), buf.readVarInt());
		}

		@Override
		public void writeJson(Properties properties, JsonObject json) {
			json.addProperty("type", properties.type.name());
			json.addProperty("minLength", properties.minLength);
			json.addProperty("maxLength", properties.maxLength);
		}
	}

	public static class Properties implements ArgumentTypeProperties<CrampedStringArgumentType, String, Properties> {
		public final StringArgumentType.StringType type;
		public final int minLength, maxLength;

		public Properties(StringArgumentType.StringType type, int minLength, int maxLength) {
			this.type = type;
			this.minLength = minLength;
			this.maxLength = maxLength;
		}

		@Override
		public CrampedStringArgumentType createType() {
			return new CrampedStringArgumentType(type == StringArgumentType.StringType.SINGLE_WORD ? StringArgumentType.word() :
					type == StringArgumentType.StringType.QUOTABLE_PHRASE ? StringArgumentType.string() : StringArgumentType.greedyString(), minLength, maxLength);
		}

		@Override
		public ArgumentTypeSerialiser<CrampedStringArgumentType, String, Properties> getSerialiser() {
			return SERIALISER;
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeByte(type.ordinal());
			buf.writeVarInt(minLength);
			buf.writeVarInt(maxLength);
		}
	}
}
