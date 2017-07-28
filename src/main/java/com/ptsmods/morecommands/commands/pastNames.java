package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class pastNames {

	public pastNames() {
	}

	public static class CommandpastNames implements ICommand {

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1 && server != null) return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
			
		}

		public String getName() {
			return "pastnames";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			String user = "";
			if (args.length == 0) user = sender.getName();
			else user = args[0];
			try {
				String data = "";
				String firstName = "";
				HashMap<String, Long> dataMap = Reference.getPastNamesFromUUID(Reference.getUUIDFromName(user));
				if (dataMap == new HashMap<String, Long>());
				else { for (int x = 0; x < dataMap.keySet().size(); x += 1) {
					String key = (String) dataMap.keySet().toArray()[x];
					if (dataMap.get(key) == null) firstName = key;
					else data += "\nChanged to " + key + " at " + new Date(dataMap.get(key)).toString();
				}
				}
				String dataFinal = "First name: " + firstName + data;
				if (!data.equals("")) Reference.sendMessage(sender, dataFinal); else Reference.sendMessage(sender, "No data found.");
				
			} catch (IOException e) {
				Reference.sendMessage(sender, "An unknown error occured while attempting to get the player's UUID, please try again, if the error keeps occuring send PlanetTeamSpeak the error you can find in the console.");
				e.printStackTrace();
			}

		}
		
		protected String usage = "/pastnames [user] Gets all the past names of the given user.";

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