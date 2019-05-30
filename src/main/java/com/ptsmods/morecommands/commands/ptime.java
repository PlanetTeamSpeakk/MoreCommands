package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class ptime {

	public ptime() {}

	public static class Commandptime extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				ArrayList completions = new ArrayList();
				completions.add("day");
				completions.add("night");
				completions.add("normal");
				return completions;
			} else return new ArrayList();
		}

		@Override
		public String getName() {
			return "ptime";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				Integer time = -1;
				Boolean fixed = false;
				if (args[0].toLowerCase().equals("day")) {
					time = 1000;
					Reference.sendMessage(sender, "The time has been changed to 1000 ticks, aka day-time.");
				} else if (args[0].toLowerCase().equals("night")) {
					time = 13000;
					Reference.sendMessage(sender, "The time has been changed to 13000 ticks, aka night-time.");
				} else if (args[0].toLowerCase().equals("normal")) {
					time = -1;
					Reference.sendMessage(sender, "The time has been changed to the server time.");
				} else if (Reference.isInteger(args[0])) {
					time = Integer.parseInt(args[0]);
					Reference.sendMessage(sender, "The time has been changed to " + args[0] + " ticks.");
				} else if (args[0].equals("@day")) {
					time = 1000;
					fixed = true;
					Reference.sendMessage(sender, "The time has been fixed to 1000 ticks, aka day-time.");
				} else if (args[0].equals("@night")) {
					time = 13000;
					fixed = true;
					Reference.sendMessage(sender, "The time has been fixed to 13000 ticks, aka night-time.");
				} else if (args[0].startsWith("@") && Reference.isInteger(args[0].substring(1))) {
					time = Integer.parseInt(args[0].substring(1));
					fixed = true;
					Reference.sendMessage(sender, "The time has been fixed to " + args[0].substring(1) + " ticks.");
				} else Reference.sendCommandUsage(sender, usage);

				Commandptime.time = time;
				Commandptime.fixed = fixed;
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		public static int		time	= -1;
		public static boolean	fixed	= false;

		protected String usage = "/ptime <day/night/normal/6900> Sets your personal time, putting an @ in front of the time will make it fixed, e.g. /ptime @day.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}