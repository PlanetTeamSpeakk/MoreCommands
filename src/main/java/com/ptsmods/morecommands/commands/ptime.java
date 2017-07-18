package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class ptime {

	public static Object instance;

	public ptime() {
	}

	public static class Commandptime implements ICommand {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				ArrayList completions = new ArrayList();
				completions.add("day");
				completions.add("night");
				completions.add("normal");
				return completions;
			} else {
				return new ArrayList();
			}
		}

		public String getName() {
			return "ptime";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) {
				Reference.sendCommandUsage(sender, usage);
			} else {
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
				} else {
					Reference.sendCommandUsage(sender, usage);
				}
				
				this.time = time;
				this.fixed = fixed;
			}

		}
		
		public static int time = -1;
		public static boolean fixed = false;
		
		protected String usage = "/ptime <day/night/normal/6900> Sets your personal time, putting an @ in front of the time will make it fixed, e.g. /ptime @day. Works the same as with Essentials but without the colors,"
				+ Reference.AQUA + " and" + Reference.BLACK + " not" + Reference.BLUE + " because" + Reference.DARK_AQUA + " I" + Reference.DARK_BLUE + " can't" + Reference.DARK_GRAY + ".";

		@Override
		public int compareTo(ICommand arg0) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return false;
		}

	}

}