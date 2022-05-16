package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor(staticName = "hexInt")
public class HexIntegerArgumentType implements CompatArgumentType<HexIntegerArgumentType, String, ConstantSerialiser.ConstantProperties<HexIntegerArgumentType, String>> {
	public static final ConstantSerialiser<HexIntegerArgumentType, String> SERIALISER = new ConstantSerialiser<>(HexIntegerArgumentType::new);
	private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(LiteralTextBuilder.builder("The given value is not a (hexa)decimal number between #000000 and #FFFFFF").build());

	public static int getHexInt(CommandContext<?> ctx, String argName) {
		return Integer.parseInt(ctx.getArgument(argName, String.class));
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		String s = MoreCommands.readTillSpaceOrEnd(reader);
		if (s.startsWith("#")) s = s.substring(1);
		else if (s.toLowerCase().startsWith("0x")) s = s.substring(2);

		int i;
		if (MoreCommands.isInteger(s) && (i = Integer.parseInt(s)) >= 0 && i <= 0xFFFFFF) return "" + i;
		else if (MoreCommands.isInteger(s, 16) && (i = Integer.parseInt(s, 16)) >= 0 && i <= 0xFFFFFF) return "" + i;

		throw exc.createWithContext(reader);
	}

	@Override
	public Collection<String> getExamples() {
		return ImmutableList.of("#FFAA00", "FFAA00", "" + 0xFFFFFF);
	}

	@Override
	public ArgumentType<String> toVanillaArgumentType() {
		return StringArgumentType.word();
	}

	@Override
	public ConstantSerialiser.ConstantProperties<HexIntegerArgumentType, String> getProperties() {
		return SERIALISER.getProperties();
	}
}
