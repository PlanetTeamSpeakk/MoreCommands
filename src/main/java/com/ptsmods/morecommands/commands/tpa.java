package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class tpa {

	public tpa() {
	}

	public static class Commandtpa implements ICommand {

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
		}

		public String getName() {
			return "tpa";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {
				Reference.sendCommandUsage(sender, usage);
			} else if (args[0].equals(sender.getName())) {
				Reference.sendMessage(sender, "You cannot send a tpa request to yourself.");
			} else {
				try {
					EntityPlayer victim = CommandBase.getPlayer(server, sender, args[0]);
					Reference.tpRequests.put(victim.getName(), sender.getName());
					Reference.sendMessage(victim, sender.getName() + " has sent you a tpa request, to accept type /tpaccept.");
					Reference.sendMessage(sender, "A tpa request has been sent to " + victim.getName() + ".");
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The given player could not be found.");
				}
			}

		}
		
		protected String usage = "/tpa <player> Send someone a request to teleport to them.";

		@Override
		public int compareTo(ICommand o) {
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