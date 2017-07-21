// THIS IS A DUMMY CLASS MEANING IT WON'T BE LOADED INTO THE GAME.
// THIS CLASS IS MEANT TO COPY AND PASTE TO MAKE NEW COMMANDS.

package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class sudo {

	public static Object instance;

	public sudo() {
	}

	public static class Commandsudo extends CommandBase {
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
					EntityPlayer victim = getPlayer(server, sender, args[0]);
					final List<String> argslist = new ArrayList<String>();
					Collections.addAll(argslist, args);
					argslist.remove(victim.getName());
					args = argslist.toArray(new String[argslist.size()]);
					String command = "";
					for (int x = 0; x < args.length; x += 1) {
						if (x != 0) command += " ";
						command += args[x];
					}
					server.getCommandManager().executeCommand(victim, command);
					Reference.sendMessage(sender, "The command " + TextFormatting.GRAY + TextFormatting.ITALIC + command + TextFormatting.RESET + " has been executed as " + victim.getName() + ".");
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The given player could not be found.");
				}
				
			}

		}
		
		protected String usage = "/sudo <player> <command> Runs a command as another player.";

	}

}