package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class invsee {

	public invsee() {}

	public static class Commandinvsee extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("inventorysee");
			aliases.add("seeinv");
			aliases.add("seeinventory");
			aliases.add("invs");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "invsee";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				EntityPlayer player;
				try {
					player = getPlayer(server, sender, args[0]);
				} catch (CommandException e) {
					Reference.sendMessage(sender, TextFormatting.RED + "The given player could not be found.");
					return;
				}
				getCommandSenderAsPlayer(sender).displayGUIChest(player.inventory); // I am surprised it is THAT easy.
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "invsee", "Allows you to see and change another player's inventory.", true);
		}

		private String usage = "/invsee <player> Allows you to see and change another player's inventory.";

	}

}