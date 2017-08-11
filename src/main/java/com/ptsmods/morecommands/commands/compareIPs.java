package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class compareIPs {

	public compareIPs() {
	}

	public static class CommandcompareIPs extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length <= 2) return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
		}

		public String getName() {
			return "compareips";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 2) {
				EntityPlayerMP player1;
				EntityPlayerMP player2;
				try {
					player1 = CommandBase.getPlayer(server, sender, args[0]);
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The first given player could not be found.");
					return;
				}
				try {
					player2 = CommandBase.getPlayer(server, sender, args[1]);
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The second given player could not be found.");
					return;
				}
				if (player1 == player2) Reference.sendMessage(sender, "Please give 2 different players.");
				else Reference.sendMessage(sender, "The ip addresses of " + player1.getName() + " and " + player2.getName() + " are" + (player1.getPlayerIP().equals(player2.getPlayerIP()) ? "" : " not") + " the same.");
			} else Reference.sendCommandUsage(sender, usage);

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/compareips <player1> <player2> Compares the ip addresses of 2 players to see if an account is an alt account of someone.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}