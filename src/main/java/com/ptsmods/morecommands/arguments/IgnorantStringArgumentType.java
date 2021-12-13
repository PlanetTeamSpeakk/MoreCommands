package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;

// Doesn't care what characters you put in an unquoted string.
public class IgnorantStringArgumentType implements ArgumentType<String>, ServerSideArgumentType {

	private final StringArgumentType.StringType type;

	private IgnorantStringArgumentType(StringArgumentType.StringType type) {
		this.type = type;
	}

	public static IgnorantStringArgumentType word() {
		return new IgnorantStringArgumentType(StringArgumentType.StringType.SINGLE_WORD);
	}

	public static IgnorantStringArgumentType string() {
		return new IgnorantStringArgumentType(StringArgumentType.StringType.QUOTABLE_PHRASE);
	}

	public static IgnorantStringArgumentType greedyString() {
		return new IgnorantStringArgumentType(StringArgumentType.StringType.GREEDY_PHRASE);
	}

	public static String getString(CommandContext<?> context, String name) {
		return context.getArgument(name, String.class);
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
		} else {
			return this.type == StringArgumentType.StringType.SINGLE_WORD ? MoreCommands.readTillSpaceOrEnd(reader) : reader.readString();
		}
	}

	@Override
	public Collection<String> getExamples() {
		return ImmutableList.of("Yeet", "&1");
	}

	@Override
	public ArgumentType<?> toVanillaArgumentType() {
		return type == StringArgumentType.StringType.GREEDY_PHRASE ? StringArgumentType.greedyString() : type == StringArgumentType.StringType.SINGLE_WORD ? StringArgumentType.word() : StringArgumentType.string();
	}

	public static class Serialiser implements ArgumentSerializer<IgnorantStringArgumentType> {
		public Serialiser() {
		}

		public void toPacket(IgnorantStringArgumentType stringArgumentType, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeEnumConstant(stringArgumentType.getType());
		}

		public IgnorantStringArgumentType fromPacket(PacketByteBuf packetByteBuf) {
			StringArgumentType.StringType stringType = packetByteBuf.readEnumConstant(StringArgumentType.StringType.class);
			switch(stringType) {
				case SINGLE_WORD:
					return IgnorantStringArgumentType.word();
				case QUOTABLE_PHRASE:
					return IgnorantStringArgumentType.string();
				case GREEDY_PHRASE:
				default:
					return IgnorantStringArgumentType.greedyString();
			}
		}

		public void toJson(IgnorantStringArgumentType stringArgumentType, JsonObject jsonObject) {
			switch(stringArgumentType.getType()) {
				case SINGLE_WORD:
					jsonObject.addProperty("type", "word");
					break;
				case QUOTABLE_PHRASE:
					jsonObject.addProperty("type", "phrase");
					break;
				case GREEDY_PHRASE:
				default:
					jsonObject.addProperty("type", "greedy");
			}

		}
	}

}
