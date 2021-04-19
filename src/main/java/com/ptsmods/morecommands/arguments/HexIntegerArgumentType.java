package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.text.LiteralText;

import java.util.Collection;

public class HexIntegerArgumentType implements ArgumentType<Integer> {

	private static final SimpleCommandExceptionType exc = new SimpleCommandExceptionType(new LiteralText("The given value is not a (hexa)decimal number between #000000 and #FFFFFF"));

	@Override
	public Integer parse(StringReader reader) throws CommandSyntaxException {
		String s = MoreCommands.readTillSpaceOrEnd(reader);
		if (s.startsWith("#")) s = s.substring(1);
		else if (s.toLowerCase().startsWith("0x")) s = s.substring(2);
		if (MoreCommands.isInteger(s) && Integer.parseInt(s) >= 0 && Integer.parseInt(s) <= 0xFFFFFF) return Integer.parseInt(s);
		else if (MoreCommands.isInteger(s, 16) && Integer.parseInt(s, 16) >= 0 && Integer.parseInt(s, 16) <= 0xFFFFFF) return Integer.parseInt(s, 16);
		throw exc.createWithContext(reader);
	}

	@Override
	public Collection<String> getExamples() {
		return ImmutableList.of("#FFAA00", "FFAA00", "" + 0xFFFFFF);
	}
}
