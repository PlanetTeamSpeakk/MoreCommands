package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class alias {

	public alias() {}

	public static class Commandalias extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "alias";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0 || !args[0].equals("create") && !args[0].equals("edit") && !args[0].equals("delete") && !args[0].equals("list")) Reference.sendCommandUsage(sender, usage);
			else if (args.length == 1) {
				if (args[0].equals("list")) {
					if (Reference.getAliases().isEmpty()) Reference.sendMessage(sender, "You haven't made any aliases yet, you can make one with /alias add <name> <command>.");
					else Reference.sendMessage(sender, "You have created the following aliases:\n" + TextFormatting.GOLD + Reference.joinCustomChar(TextFormatting.YELLOW + ", " + TextFormatting.GOLD, Reference.getAliases().keySet().toArray(new String[0])));
				} else if (args[0].equals("create")) Reference.sendCommandUsage(sender, "/alias create <name> Creates an alias, you can assign a command to it with /alias edit <name> <command>.");
				else if (args[0].equals("delete")) Reference.sendCommandUsage(sender, "/alias delete <name> Deletes an alias.");
				else if (args[0].equals("edit")) Reference.sendCommandUsage(sender, "/alias edit <name> <command> Edits an alias.");
			} else if (args[0].equals("create")) {
				Reference.createAlias(args[1]);
				Reference.sendMessage(sender, "The alias " + args[1] + " has been made, you can assign a command to it with /alias edit " + args[1] + " <command>.");
			} else if (args[0].equals("delete")) {
				if (Reference.doesAliasExist(args[1])) {
					Reference.removeAlias(args[1]);
					Reference.sendMessage(sender, "The alias " + args[1] + " has been removed.");
				}
			} else if (args[0].equals("edit"))
				if (args.length == 2) Reference.sendCommandUsage(sender, "/alias edit <name> <command> Edits an alias.");
				else if (Reference.doesAliasExist(args[1])) {
				Reference.editAlias(args[1], Reference.join(Reference.removeArgs(args, 0, 1)));
				Reference.sendMessage(sender, "The alias " + args[1] + " has been edited to run " + Reference.join(Reference.removeArgs(args, 0, 1)) + ".");
				}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		@Override
		public Permission getPermission() {
			return new Permission(null, null, "", false);
		}

		private String usage = "/alias <create|edit|delete|list> Manage client sided aliases, can be used on any server with any command.";

	}

}