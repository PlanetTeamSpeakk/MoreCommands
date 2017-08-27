package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Initialize;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IncorrectCommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class reloadMoreCommands {

	public reloadMoreCommands() {
	}

	public static class CommandreloadMoreCommands extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("rmc");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "reloadmorecommands";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (Reference.getServerStartingEvent() == null)
				Reference.sendMessage(sender, "The MoreCommands commands are not yet registered.");
			else {
				Reference.resetBlockBlackAndWhitelist();
				try {
					Reference.resetCommandRegistry(CommandType.CLIENT);
					Reference.resetCommandRegistry(CommandType.SERVER);
				} catch (IncorrectCommandType e) {
					e.printStackTrace();
				}
				Initialize.setupCommandRegistry();
				Initialize.registerCommands(Reference.getServerStartingEvent());
				Reference.sendMessage(sender, "MoreCommands commands have successfully been reloaded.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "rmc", "Permission to use the reloadmorecommands command.", true);
		}

		protected String usage = "/reloadmorecommands Reloads all MoreCommands commands, only PlanetTeamSpeak, Forge Test Environment accounts and the console may use this.";

	}

}