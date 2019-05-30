package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;

public class gm {

	public static Object instance;

	public gm() {}

	public static class Commandgm extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			ArrayList gamemodes = new ArrayList();
			gamemodes.add("0");
			gamemodes.add("1");
			gamemodes.add("2");
			gamemodes.add("3");
			gamemodes.add("s");
			gamemodes.add("c");
			gamemodes.add("a");
			gamemodes.add("sp");
			gamemodes.add("survival");
			gamemodes.add("creative");
			gamemodes.add("adventure");
			gamemodes.add("spectator");
			gamemodes.add("debugmode");
			if (args.length == 1) return gamemodes;
			else if (args.length == 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "gm";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException, CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			if (args.length == 0) Reference.sendCommandUsage(player, usage);
			else if (args.length == 1) {
				GameType gametype = getGameModeFromCommand(sender, args[0]);
				player.setGameType(gametype);
				Reference.sendMessage(sender, "You have been put in " + gametype.getName() + " mode.");
			} else {
				GameType gametype = getGameModeFromCommand(sender, args[0]);
				EntityPlayer victim = getPlayer(server, sender, args[1]); // I know I should put victim here, you troll.
				if (victim == null) Reference.sendMessage(sender, "The given player does not exist.");
				else {
					victim.setGameType(gametype);
					if (victim != player) {
						victim.sendMessage(new TextComponentString(player.getName() + " has put you in " + gametype.getName() + " mode."));
						Reference.sendMessage(sender, victim.getName() + " has been put in " + gametype.getName() + " mode.");
					} else Reference.sendMessage(sender, "You have been put in " + gametype.getName() + " mode.");
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "gm", "Permission to use the gm command.", true);
		}

		// Copied from net.minecraft.command.CommandGameMode:75
		protected GameType getGameModeFromCommand(ICommandSender sender, String gameModeString) throws CommandException, NumberInvalidException {
			GameType gametype = GameType.parseGameTypeWithDefault(gameModeString, GameType.NOT_SET);
			return gametype == GameType.NOT_SET ? WorldSettings.getGameTypeById(parseInt(gameModeString, 0, GameType.values().length - 2)) : gametype;
		}

		protected String getGameModeNameFromAbbreviation(String abbreviation) {
			if (abbreviation.equals("s") || abbreviation.equals("0")) abbreviation = "survival";
			else if (abbreviation.equals("c") || abbreviation.equals("1")) abbreviation = "creative";
			else if (abbreviation.equals("a") || abbreviation.equals("2")) abbreviation = "adventure";
			else if (abbreviation.equals("sp") || abbreviation.equals("3")) abbreviation = "spectator";
			else abbreviation = "unknown";
			return abbreviation;
		}

		protected String usage = "/gm <gamemode> [player] Sets your gamemode or the gamemode of the given player.";

	}

}