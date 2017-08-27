package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class moreCommandsPermissions {

	public moreCommandsPermissions() {
	}

	public static class CommandmoreCommandPermissions extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("morecommandspermissions");
			aliases.add("morecommandsperms");
			aliases.add("morecp");
			aliases.add("mcp"); // ModCoderPack :3. Just FYI, this was not intentional.
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			List<String> completions = new ArrayList<>();
			if (args.length == 1) {
				completions.add("group");
				completions.add("player");
				completions.add("listperms");
				completions = getListOfStringsMatchingLastWord(args, completions);
			} else if (args.length == 2) {
				if (args[0].equals("group")) {
					completions.add("create");
					completions.add("delete");
					completions.add("addperm");
					completions.add("delperm");
					completions.add("list");
					completions = getListOfStringsMatchingLastWord(args, completions);
				} else if (args[0].equals("player")) {
					completions.add("addgroup");
					completions.add("delgroup");
					completions.add("listgroups");
					completions = getListOfStringsMatchingLastWord(args, completions);
				}
			} else if (args.length == 3) {
				if (args[0].equals("group")) {
					if (args[1].equals("delete") || args[1].equals("addperm"))
						completions.addAll(getListOfStringsMatchingLastWord(args, Reference.getGroups().keySet()));
					else if (args[0].equals("delperm"))
						completions.addAll(getListOfStringsMatchingLastWord(args, Permission.permissions));
				} else if (args[0].equals("player"))
					if (args[1].equals("addgroup") || args[1].equals("delgroup"))
						completions.addAll(getListOfStringsMatchingLastWord(args, Reference.getGroups().keySet()));
			} else if (args.length == 4)
				if (args[0].equals("group")) {
					if (args[1].equals("delete") || args[1].equals("addperm"))
						completions.addAll(getListOfStringsMatchingLastWord(args, Permission.permissions));
				} else if (args[0].equals("player"))
					if (args[1].equals("addgroup") || args[1].equals("delgroup"))
						completions.addAll(getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()));
			return completions;
		}

		@Override
		public String getName() {
			return "mcperms";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0 || !args[0].equals("group") && !args[0].equals("player") && !args[0].equals("listperms")) Reference.sendCommandUsage(sender, usage);
			else if (args[0].equals("group")) {
				if (args.length == 1 || !args[1].equals("create") && !args[1].equals("delete") && !args[1].equals("addperm") && !args[1].equals("delperm") && !args[1].equals("list")) Reference.sendCommandUsage(sender, "/mcperms group <create|delete|addperm|delperm|list> Manages MoreCommands groups.");
				else if (args[1].equals("create"))
					if (args.length == 2) Reference.sendCommandUsage(sender, "/mcperms group create <name> Creates a group.");
					else {
						Reference.createGroup(args[2]);
						Reference.sendMessage(sender, "The group " + args[2] + " has been created, you can add permissions to it with /mcperms group addperm or list perms with /mcperms listperms.");
					}
				else if (args[1].equals("delete")) {
					if (args.length == 2) Reference.sendCommandUsage(sender, "/mcperms group delete <name> Deletes a group.");
					else if (Reference.doesGroupExist(args[2])) {
						Reference.removeGroup(args[2]);
						Reference.sendMessage(sender, "The group has been removed.");
					} else Reference.sendMessage(sender, "The given group does not exists, you can list all existing groups with /mcperms group list.");
				} else if (args[1].equals("addperm")) {
					if (args.length == 2 || args.length == 3) Reference.sendCommandUsage(sender, "/mcperms group addperm <group> <perm> Adds a permission to a group.");
					else if (Permission.getPermissionByName(args[3]) == null) Reference.sendMessage(sender, "The given permission does not exist, you can list them with /mcperms listperms.");
					else if (!Reference.doesGroupExist(args[2])) Reference.sendMessage(sender, "The given group does not exist, you can list them with /mcperms group list.");
					else {
						Reference.addPermissionToGroup(Permission.getPermissionByName(args[3]), args[2]);
						Reference.sendMessage(sender, "The permission has been added to the group.");
					}
				} else if (args[1].equals("delperm")) {
					if (args.length == 2 || args.length == 3) Reference.sendCommandUsage(sender, "/mcperms group delperm <group> <perm> Deletes a permission from a group.");
					else if (Permission.getPermissionByName(args[3]) == null) Reference.sendMessage(sender, "The given permission does not exist, you can list them with /mcperms listperms.");
					else if (!Reference.doesGroupExist(args[2])) Reference.sendMessage(sender, "The given group does not exist, you can list them with /mcperms group list.");
					else {
						Reference.removePermissionFromGroup(Permission.getPermissionByName(args[3]), args[2]);
						Reference.sendMessage(sender, "The permission has been removed from the group.");
					}
				} else if (args[1].equals("list"))
					if (Reference.getGroups().equals(new HashMap<String, ArrayList<String>>())) Reference.sendMessage(sender, "There are no groups available yet, create one with /mcperms group create <name>.");
					else
						Reference.sendMessage(sender, "Currently available groups:\n" + TextFormatting.GOLD + Reference.joinCustomChar(TextFormatting.YELLOW + ", " + TextFormatting.GOLD, Reference.getGroups().keySet().toArray(new String[0])));
			} else if (args[0].equals("player")) {
				if (args.length == 1 || !args[1].equals("addgroup") && !args[1].equals("delgroup") && !args[1].equals("listgroups")) Reference.sendCommandUsage(sender, "/mcperms player <addgroup|delgroup|listgroups> Manages groups of a player.");
				else if (args[1].equals("addgroup")) {
					if (args.length == 2 || args.length == 3) Reference.sendCommandUsage(sender, "/mcperms player addgroup <group> <player> Adds a group to a player.");
					else if (!Reference.doesGroupExist(args[2])) Reference.sendMessage(sender, "The given group does not exist.");
					else if (Reference.getPlayer(server, args[3]) == null) Reference.sendMessage(sender, "The given player does not exist.");
					else {
						Reference.addPlayerToGroup(Reference.getPlayer(server, args[3]), args[2]);
						Reference.sendMessage(sender, "The player has been added to the group.");
					}
				} else if (args[1].equals("delgroup")) {
					if (args.length == 2 || args.length == 3) Reference.sendCommandUsage(sender, "/mcperms player delgroup <group> <player> Removes a group from a player.");
					else if (!Reference.doesGroupExist(args[2])) Reference.sendMessage(sender, "The given group does not exist.");
					else if (Reference.getPlayer(server, args[3]) == null) Reference.sendMessage(sender, "The given player does not exist.");
					else {
						Reference.removePlayerFromGroup(Reference.getPlayer(server, args[3]), args[2]);
						Reference.sendMessage(sender, "The player has been removed from the group.");
					}
				} else if (args[1].equals("listgroups"))
					if (args.length == 2) Reference.sendCommandUsage(sender, "/mcperms player listgroups <player> Lists all the group the given player is in.");
					else if (Reference.getPlayers().containsKey(((EntityPlayer) sender).getUniqueID().toString())) {
						ArrayList<String> groups = Reference.getPlayers().get(((EntityPlayer) sender).getUniqueID().toString());
						Reference.sendMessage(sender, "The given player has the following groups:\n" + TextFormatting.GOLD + Reference.joinCustomChar(TextFormatting.YELLOW + ", " + TextFormatting.GOLD, groups.toArray(new String[groups.size()])));
					} else Reference.sendMessage(sender, "The given player is not in any group.");
			} else if (args[0].equals("listperms"))
				if (Permission.permissions.equals(new ArrayList<Permission>())) Reference.sendMessage(sender, "There are no permissions."); // very, very unlikely.
				else
					Reference.sendMessage(sender, "Currently available permissions:\n" + getPermissionsString());
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(null, null, "", false);
		}

		public boolean checkPermissions(MinecraftServer server, ICommandSender sender) {
			return sender.canUseCommand(getRequiredPermissionLevel(), getName());
		}

		private String usage = "/mcperms <group|player> Manage MoreCommands permissions.";

		private static String getPermissionsString() {
			String output = "" + TextFormatting.GOLD;
			for (Permission perm : Permission.permissions)
				if (!perm.getName().equals("PERMISSION")) output += perm.toString() + TextFormatting.YELLOW + " " + perm.getDescription() + "\n" + TextFormatting.GOLD;
			return output.substring(0, output.length()-("\n" + TextFormatting.GOLD).length());
		}

	}

}