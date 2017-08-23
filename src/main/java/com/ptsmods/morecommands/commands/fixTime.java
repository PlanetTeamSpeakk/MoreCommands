package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class fixTime {

	public fixTime() {
	}

	public static class CommandfixTime extends net.minecraft.command.CommandTime {
		
		@Override
		public java.util.List getAliases() {
			List<String> aliases = super.getAliases();
			aliases.add("tiem");
			return aliases;
		}
		
		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			ArrayList options = new ArrayList();
			options.add("day");
			options.add("night");
			if (args.length != 2) {
				Reference.sendCommandUsage(sender, usage);
			} else if (!args[0].equals("query") && !args[0].equals("fix")) {
				this.time = -1;
				super.execute(server, sender, args);
			} else if (!Reference.isInteger(args[1]) && !options.contains(args[1])) {
				Reference.sendCommandUsage(sender, usage + " For fix the value has to be day, night or a number.");
			} else if (options.contains(args[1])) {
				if (args[1].equals("day")) {this.time = 1000; Reference.sendMessage(sender, "The time has been fixed to 1000 ticks, aka day-time.");}
				else if (args[1].equals("night")) {this.time = 13000; Reference.sendMessage(sender, "The time has been fixed to 13000 ticks, aka night-time");}
			} else if (Reference.isInteger(args[1])) {
				this.time = Integer.parseInt(args[1]);
				Reference.sendMessage(sender, "The time has been fixed to " + ((Integer) time).toString() + " ticks.");
			}
			
			this.server = sender.getServer();

		}
		
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		public static int time = -1;
		public static MinecraftServer server = null;
		
		protected String usage = "/time <set|add|fix|query> <value>";

		public void setAllWorldTimes(MinecraftServer server, int time) {
			super.setAllWorldTimes(server, time);
		}
		
	}

}