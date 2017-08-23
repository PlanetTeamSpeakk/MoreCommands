package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class sudo {

	public sudo() {
	}

	public static class Commandsudo extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			} else return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "sudo";
		}

		public String getUsage(ICommandSender sender) {
			return this.usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {
				Reference.sendCommandUsage(sender, usage);
			} else {
				try {
					Entity victim = getEntity(server, sender, args[0]);
					server.getCommandManager().executeCommand(victim, Reference.join(Reference.removeArg(args, 0)));
					Reference.sendMessage(sender, "The command " + TextFormatting.GRAY + TextFormatting.ITALIC + Reference.join(Reference.removeArg(args, 0)) + TextFormatting.RESET + " has been executed as " + victim.getName() + ".");
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The given player could not be found.");
				}
				
			}

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/sudo <player> <command> Runs a command as another player.";

	}

}