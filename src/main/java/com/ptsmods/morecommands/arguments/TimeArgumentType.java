package com.ptsmods.morecommands.arguments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TimeArgumentType implements CompatArgumentType<TimeArgumentType, String, ConstantSerialiser.ConstantProperties<TimeArgumentType, String>> {
	public static final ConstantSerialiser<TimeArgumentType, String> SERIALISER = new ConstantSerialiser<>(TimeArgumentType::new);
	private static final SimpleCommandExceptionType PARSE_EXCEPTION = new SimpleCommandExceptionType(() -> "The given value could not be parsed into time");
	private static final SimpleCommandExceptionType HOURS_EXCEPTION = new SimpleCommandExceptionType(() -> "Days only have 24 hours, silly");
	private static final SimpleCommandExceptionType MINUTES_EXCEPTION = new SimpleCommandExceptionType(() -> "Hours only have 60 minutes, silly");
	private static final SimpleCommandExceptionType TICKS_EXCEPTION = new SimpleCommandExceptionType(() -> "Minecraft days only have 24000 ticks, silly");
	private static final Map<String, Integer> literals = ImmutableMap.<String, Integer>builder().put("day", 1000).put("noon", 6000).put("night", 13000).put("midnight", 18000).build();

	private TimeArgumentType() {}

	public static TimeArgumentType time() {
		return new TimeArgumentType();
	}

	public static WorldTime getTime(CommandContext<?> ctx, String argName) {
		String timeString = ctx.getArgument(argName, String.class);

		boolean fixed = timeString.startsWith("@");
		return new WorldTime(Integer.parseInt(fixed ? timeString.substring(1) : timeString), fixed);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		String s = MoreCommands.readTillSpaceOrEnd(reader);
		if (MoreCommands.isInteger(s)) {
			int i = Integer.parseInt(s);
			if (i >= 24000) throw TICKS_EXCEPTION.createWithContext(reader);
			else return "" + i;
		} else if (s.startsWith("@") && MoreCommands.isInteger(s.substring(1))) {
			int i = Integer.parseInt(s.substring(1));
			if (i >= 24000) throw TICKS_EXCEPTION.createWithContext(reader);
			else return "@" + i;
		} else if (s.contains(":")) {
			boolean fixed = s.startsWith("@");
			if (fixed) s = s.substring(1);
			int hours = 0;
			int minutes = 0;
			if (s.endsWith("pm") || s.endsWith("am")) {
				s = s.substring(0, s.length()-2);
				if (s.endsWith("pm")) hours += 11;
			}
			hours += Integer.parseInt(s.split(":")[0]);
			minutes += Integer.parseInt(s.split(":")[1]);
			if (hours >= 24) throw HOURS_EXCEPTION.createWithContext(reader);
			else if (minutes >= 60) throw MINUTES_EXCEPTION.createWithContext(reader);
			return (fixed ? "@" : "") + timeToTicks(hours, minutes);
		} else {
			boolean fixed = s.startsWith("@");
			if (fixed) s = s.substring(1);
			if (literals.containsKey(s.toLowerCase())) return (fixed ? "@" : "") + literals.get(s.toLowerCase());
		}
		throw PARSE_EXCEPTION.createWithContext(reader);
	}

	private int timeToTicks(int hours, int minutes) {
		int ret = 18000;
		ret += (hours) * 1000;
		ret += (minutes / 60.0) * 1000;
		ret %= 24000;
		return ret;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String s = builder.getRemaining().toLowerCase();
		if (s.startsWith("@")) s = s.substring(1);
		if (MoreCommands.isInteger(s)) {
			int i = Integer.parseInt(s);
			if (i < 24000) {
				if (i < 24)
					for (int x = 0; x < 6; x++) {
						String s0 = i + ":" + x + "0";
						suggestTime(builder, i, s0);
					}
				builder.suggest(s);
				builder.suggest("@" + s);
			}
		} else if (s.contains(":") && MoreCommands.isInteger(s.split(":")[0]) && (s.split(":").length == 1 || MoreCommands.isInteger(s.split(":")[1]))) {
			int hours = Integer.parseInt(s.split(":")[0]);
			int minutes = s.split(":").length == 1 ? 0 : Integer.parseInt(s.split(":")[1]);
			String s0 = hours + ":" + (minutes == 0 ? "00" : minutes < 6 ? minutes*10 : minutes < 10 ? "0" + minutes : minutes);
			if (hours < 24) suggestTime(builder, hours, s0);
		} else {
			for (String literal : literals.keySet())
				if (literal.startsWith(s)) {
					builder.suggest(literal);
					builder.suggest("@" + literal);
				}
		}
		return builder.buildFuture();
	}

	private void suggestTime(SuggestionsBuilder builder, int hours, String time) {
		builder.suggest(time);
		builder.suggest("@" + time);
		if (hours <= 12) {
			builder.suggest(time + "am");
			builder.suggest("@" + time + "am");
			builder.suggest(time + "pm");
			builder.suggest("@" + time + "pm");
		}
	}

	@Override
	public Collection<String> getExamples() {
		return Lists.newArrayList("18000", "@12000", "12:30");
	}

	@Override
	public ArgumentType<String> toVanillaArgumentType() {
		return StringArgumentType.word();
	}

	@Override
	public ConstantSerialiser.ConstantProperties<TimeArgumentType, String> getProperties() {
		return SERIALISER.getProperties();
	}

	public static class WorldTime {

		private final int time;
		private final boolean fixed;

		public WorldTime(int time, boolean fixed) {
			this.time = time;
			this.fixed = fixed;
		}

		public int getTime() {
			return time;
		}

		public boolean isFixed() {
			return fixed;
		}
	}
}
