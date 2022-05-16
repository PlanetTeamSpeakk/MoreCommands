package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;

// Doesn't care what characters you put in an unquoted string.
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IgnorantStringArgumentType implements ArgumentType<String>, CompatArgumentType<IgnorantStringArgumentType, String, IgnorantStringArgumentType.Properties> {
	public static final Serialiser SERIALISER = new Serialiser();
	private final StringArgumentType.StringType type;

	public static IgnorantStringArgumentType word() {
		return new IgnorantStringArgumentType(StringArgumentType.StringType.SINGLE_WORD);
	}

	public static IgnorantStringArgumentType string() {
		return new IgnorantStringArgumentType(StringArgumentType.StringType.QUOTABLE_PHRASE);
	}

	public static IgnorantStringArgumentType greedyString() {
		return new IgnorantStringArgumentType(StringArgumentType.StringType.GREEDY_PHRASE);
	}

	public StringArgumentType.StringType getType() {
		return this.type;
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		if (this.type == StringArgumentType.StringType.GREEDY_PHRASE) {
			String text = reader.getRemaining();
			reader.setCursor(reader.getTotalLength());
			return text;
		} else return this.type == StringArgumentType.StringType.SINGLE_WORD ? MoreCommands.readTillSpaceOrEnd(reader) : reader.readString();
	}

	@Override
	public Collection<String> getExamples() {
		return ImmutableList.of("Yeet", "&1");
	}

	@Override
	public ArgumentType<String> toVanillaArgumentType() {
		return type == StringArgumentType.StringType.GREEDY_PHRASE ? StringArgumentType.greedyString() : type == StringArgumentType.StringType.SINGLE_WORD ? StringArgumentType.word() : StringArgumentType.string();
	}

	@Override
	public Properties getProperties() {
		return new Properties(type);
	}

	public static class Serialiser implements ArgumentTypeSerialiser<IgnorantStringArgumentType, String, Properties> {
		private Serialiser() {}

		@Override
		public IgnorantStringArgumentType fromPacket(PacketByteBuf buf) {
			return new IgnorantStringArgumentType(buf.readEnumConstant(StringArgumentType.StringType.class));
		}

		@Override
		public void writeJson(Properties properties, JsonObject json) {
			json.addProperty("type", properties.type.name());
		}
	}

	public static class Properties implements ArgumentTypeProperties<IgnorantStringArgumentType, String, Properties> {
		public final StringArgumentType.StringType type;

		public Properties(StringArgumentType.StringType type) {
			this.type = type;
		}

		@Override
		public IgnorantStringArgumentType createType() {
			return new IgnorantStringArgumentType(type);
		}

		@Override
		public ArgumentTypeSerialiser<IgnorantStringArgumentType, String, Properties> getSerialiser() {
			return SERIALISER;
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeEnumConstant(type);
		}
	}
}
