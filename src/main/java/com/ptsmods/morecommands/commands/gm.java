package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.CommandBase;
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

	public gm() {
	}

	public static class Commandgm extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

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
			if (args.length == 1) {
				return gamemodes;
			} else if (args.length == 2) {
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			} else {
				return new ArrayList();
			}
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "gm";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException, CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			if (args.length == 0) {
				Reference.sendMessage(sender, Reference.RED + "Usage: " + usage);
			} else if (args.length == 1) {
				GameType gametype = this.getGameModeFromCommand(sender, args[0]);
				player.setGameType(gametype);
				String gamemode = this.getGameModeNameFromAbbreviation(args[0]);
				sender.sendMessage(new TextComponentString("You have been put in " + gamemode + " mode."));
			} else {
				GameType gametype = this.getGameModeFromCommand(sender, args[0]);
				EntityPlayer victim = getPlayer(server, sender, args[1]); // I know I should put victim here, you troll.
				if (victim == null) {
					sender.sendMessage(new TextComponentString("The given player does not exist."));
				} else {
					victim.setGameType(gametype);
					String gamemode = this.getGameModeNameFromAbbreviation(args[0]);
					if (victim != player) {
						victim.sendMessage(new TextComponentString(player.getName() + " has put you in " + gamemode + " mode."));
						sender.sendMessage(new TextComponentString(victim.getName() + " has been put in " + gamemode + " mode."));
					} else {
						sender.sendMessage(new TextComponentString("You have been put in " + gamemode + " mode."));
					}
				}
			}

		}
		
		// Copied from net.minecraft.command.CommandGameMode:75
	    protected GameType getGameModeFromCommand(ICommandSender sender, String gameModeString) throws CommandException, NumberInvalidException {
	        GameType gametype = GameType.parseGameTypeWithDefault(gameModeString, GameType.NOT_SET);
	        return gametype == GameType.NOT_SET ? WorldSettings.getGameTypeById(parseInt(gameModeString, 0, GameType.values().length - 2)) : gametype;
	    }
	    
	    protected String getGameModeNameFromAbbreviation(String abbreviation) {
			if (abbreviation.equals("s") || abbreviation.equals("0")) {
				abbreviation = "survival";
			} else if (abbreviation.equals("c") || abbreviation.equals("1")) {
				abbreviation = "creative";
			} else if (abbreviation.equals("a") || abbreviation.equals("2")) {
				abbreviation = "adventure";
			} else if (abbreviation.equals("sp") || abbreviation.equals("3")) {
				abbreviation = "spectator";
			} else {
				abbreviation = "unknown";
			}
			return abbreviation;
	    }
	    
	    protected String usage = "/gm <gamemode> [player] Sets your gamemode or the gamemode of the given player.";

	}

}