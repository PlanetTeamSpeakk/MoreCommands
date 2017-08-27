package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class god {

	public static Object instance;

	public god() {
	}

	public static class Commandgod extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
				ArrayList options = new ArrayList();
				options.add("on");
				options.add("off");
				return options;
			} else if (args.length == 2)
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else
				return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "god";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0 || !args[0].equals("on") && !args[0].equals("off"))
				Reference.sendCommandUsage(sender, usage);
			else {
				EntityPlayer player = (EntityPlayer) sender;
				if (args.length == 2)
					try {
						player = getPlayer(server, sender, args[1]);
					} catch (PlayerNotFoundException e) {
						Reference.sendMessage(sender, "The given player could not be found.");
					}
				if (args[0].equals("on")) {
					player.setEntityInvulnerable(true);
					Reference.sendMessage(sender, "You're now invulnerable.");
				} else {
					player.setEntityInvulnerable(false);
					Reference.sendMessage(sender, "You're no longer invulnerable.");
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "god", "Permission to use the god command.", true);
		}

		protected String usage = "/god <on/off> [player] You'll never get damage again and you'll never be hungry anymore.";

	}

}