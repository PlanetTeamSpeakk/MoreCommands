package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Initialize;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class reloadMoreCommands {

	public static Object instance;

	public reloadMoreCommands() {
	}

	public static class CommandreloadMoreCommands implements ICommand {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("rmc");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "reloadmorecommands";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (Reference.getServerStartingEvent() == null) {
				Reference.sendMessage(sender, "The MoreCommands commands are not yet registered.");
			} else {
				Reference.resetBlockBlackAndWhitelist();
				Initialize.registerCommands(Reference.getServerStartingEvent());
				Reference.sendMessage(sender, "MoreCommands commands have successfully been reloaded.");
			}
		}
		
		protected String usage = "/reloadmorecommands Reloads all MoreCommands commands, only PlanetTeamSpeak, Forge Test Environment accounts and the console may use this.";

		@Override
		public int compareTo(ICommand arg0) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			if (Reference.isConsole(sender)) return true;
			else return ((sender.getName().startsWith("Player") && Reference.isInteger(sender.getName().substring(6))) || sender.getName().equals("PlanetTeamSpeak"));
		}

	}

}