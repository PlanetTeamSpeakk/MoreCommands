package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class enderChest {

	public enderChest() {}

	public static class CommandenderChest extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("ec");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return args.length == 1 && Reference.checkPermission(sender, others) ? getListOfStringsMatchingLastWord(args, server.getPlayerList().getOnlinePlayerNames()) : Lists.newArrayList();
		}

		@Override
		public String getName() {
			return "enderchest";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		private static final Permission others = new Permission(Reference.MOD_ID, "enderchest.others", "Open other people's enderchests from anywhere.", true);

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) getCommandSenderAsPlayer(sender).displayGUIChest(getCommandSenderAsPlayer(sender).getInventoryEnderChest());
			else if (!Reference.checkPermission(sender, others)) Reference.sendMessage(sender, TextFormatting.RED + "You do not have permission to view other players' enderchests.");
			else {
				EntityPlayer player;
				try {
					player = getPlayer(server, sender, args[0]);
				} catch (CommandException e) {
					Reference.sendMessage(sender, "A player by the name of " + args[0] + " could not be found.");
					return;
				}
				getCommandSenderAsPlayer(sender).displayGUIChest(player.getInventoryEnderChest());
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "enderchest", "Open your enderchest from anywhere.", true);
		}

		private String usage = "/enderchest [player] Open your or someone elses enderchest from anywhere.";

	}

}