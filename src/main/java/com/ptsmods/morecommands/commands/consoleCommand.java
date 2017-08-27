package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class consoleCommand {

	public consoleCommand() {
	}

	public static class CommandconsoleCommand extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("ccmd");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "consolecommand";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0)
				Reference.sendCommandUsage(sender, usage);
			else {
				String command = "";
				for (Integer length = 0; length < args.length; length += 1) {
					command += args[length];
					if (length + 1 != args.length) command += " ";
				}
				server.getCommandManager().executeCommand(server, command);
				Reference.sendMessage(sender, "Successfully ran command " + TextFormatting.GRAY + TextFormatting.ITALIC + command + TextFormatting.RESET + " from the console. Some commands may give errors, or not work at all when ran by the console.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "ccmd", "Permission to use the consolecommand command.", true);
		}

		protected String usage = "/consolecommand <command> Runs a command from the console.";

	}

}