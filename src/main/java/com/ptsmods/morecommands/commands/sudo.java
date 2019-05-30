package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class sudo {

	public sudo() {}

	public static class Commandsudo extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				List<String> list = getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
				list.remove(sender.getName());
				return list;
			} else if (args.length == 2) {
				List completions = new ArrayList();
				for (ICommand command : server.getCommandManager().getPossibleCommands(sender))
					if (command.getName().startsWith(args[1])) completions.add(command.getName());
				return completions;
			} else if (args.length > 2) {
				ICommand command = null;
				for (ICommand command0 : server.getCommandManager().getPossibleCommands(sender))
					if (command0.getName().equals(args[1])) {
						command = command0;
						break;
					}
				if (command != null) return command.getTabCompletions(server, sender, Reference.removeArgs(args, 0, 1), pos);
			}
			return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "sudo";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else try {
				if (args[0].startsWith("@")) {
					List<Entity> entities = EntitySelector.matchEntities(sender, args[0], Entity.class);
					int success = 0;
					int fail = 0;
					for (Entity entity : entities)
						if (server.getCommandManager().executeCommand(entity, Reference.join(Reference.removeArg(args, 0))) >= 1) success++;
						else fail++;
					Reference.sendMessage(sender, TextFormatting.GREEN + "Successfully" + Reference.dtf + " executed the command as " + success + " entit" + (success == 1 ? "y" : "ies") + (fail == 0 ? "." : "; however, the command could " + TextFormatting.RED + "not" + Reference.dtf + " be executed as " + fail + " entit" + (fail == 1 ? "y" : "ies") + "."));
				} else {
					Entity victim = getEntity(server, sender, args[0]);
					if (sender instanceof Entity && ((Entity) sender).getUniqueID().equals(victim.getUniqueID())) Reference.sendMessage(sender, "You cannot sudo yourself, silly.");
					else {
						String command = Reference.join(Reference.removeArg(args, 0));
						if (command.startsWith("c:") && victim instanceof EntityPlayerMP) ((EntityPlayerMP) victim).connection.processChatMessage(new CPacketChatMessage(command.substring(2)));
						else server.getCommandManager().executeCommand(victim, command);
						Reference.sendMessage(sender, "The command " + TextFormatting.GRAY + TextFormatting.ITALIC + Reference.join(Reference.removeArg(args, 0)) + Reference.dtf + " has been executed as " + victim.getName() + ".");
					}
				}
			} catch (PlayerNotFoundException e) {
				Reference.sendMessage(sender, "The given player could not be found.");
			}

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "sudo", "Permission to use the sudo command.", true);
		}

		protected String usage = "/sudo <player> <command> Runs a command as another player.";

	}

}