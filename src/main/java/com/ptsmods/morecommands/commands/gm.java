// THIS IS A DUMMY CLASS MEANING IT WON'T BE LOADED INTO THE GAME.
// THIS CLASS IS MEANT TO COPY AND PASTE TO MAKE NEW COMMANDS.

package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

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
		public boolean isUsernameIndex(int var1) {
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

		public String getUsage(ICommandSender var1) {
			return "/gm <gamemode> [player] Sets your gamemode or the gamemode of the given player.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] args) throws NumberInvalidException, CommandException {
			EntityPlayer player = (EntityPlayer) var1;
			if (args.length == 0) {
				var1.sendMessage(new TextComponentString("§cUsage: /gm <gamemode [player] Sets your gamemode or the gamemode of the given player."));
			} else if (args.length == 1) {
				GameType gametype = this.getGameModeFromCommand(var1, args[0]);
				player.setGameType(gametype);
			} else {
				GameType gametype = this.getGameModeFromCommand(var1, args[0]);
				EntityPlayer victim = getPlayer(server, var1, args[1]); // I know I should put victim here, you troll.
				if (victim == null) {
					var1.sendMessage(new TextComponentString("The given player does not exist."));
				} else {
					victim.setGameType(gametype);
					if (args[0] == "s" || args[0] == "0") {
						args[0] = "survival";
					} else if (args[0] == "c" || args[0] == "1") {
						args[0] = "creative";
					} else if (args[0] == "a" || args[0] == "2") {
						args[0] = "adventure";
					} else if (args[0] == "sp" || args[0] == "3") {
						args[0] = "spectator";
					}
					victim.sendMessage(new TextComponentString(player.getName() + " has put you in " + args[0] + " mode."));
					var1.sendMessage(new TextComponentString(victim.getName() + " has been put in " + args[0] + " mode."));
				}
			}

		}
		
		// Copied from net.minecraft.command.CommandGameMode:75
	    protected GameType getGameModeFromCommand(ICommandSender sender, String gameModeString) throws CommandException, NumberInvalidException {
	        GameType gametype = GameType.parseGameTypeWithDefault(gameModeString, GameType.NOT_SET);
	        return gametype == GameType.NOT_SET ? WorldSettings.getGameTypeById(parseInt(gameModeString, 0, GameType.values().length - 2)) : gametype;
	    }

	}

}