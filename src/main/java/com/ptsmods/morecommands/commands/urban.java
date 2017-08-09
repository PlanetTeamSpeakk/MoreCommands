package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class urban {

	public urban() {
	}

	public static class Commandurban implements ICommand {

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("define");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "urban";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				String searchTerm = "";
				String data;
				for (int x = 0; x < args.length; x += 1) {
					searchTerm += args[x] + ((x+1 != args.length) ? " " : "");
				}
				try {
					data = Reference.getHTML("http://api.urbandictionary.com/v0/define?term=" + searchTerm.replaceAll(" ", "+"));
				} catch (IOException e) {
					Reference.sendMessage(sender, "An unknown error occured while trying to get the data, please try again.");
					return;
				}
				if (data.contains("\"result_type\":\"no_results\"")) {
					Reference.sendMessage(sender, "No results found for the search term " + TextFormatting.GRAY + TextFormatting.ITALIC + searchTerm + TextFormatting.RESET + ".");
				} else {
					String[] dataArray = data.split("\\[\\{");
					String[] dataArray2 = dataArray[1].split("\",\"");
					String definition = Reference.getCleanString(dataArray2[0].substring(14));
					String example = dataArray2[5];
					example = Reference.getCleanString(example.substring(10));
					String thumbsUp = dataArray2[2].split(",\"")[0].substring(11);
					String thumbsDown = dataArray2[6].substring(13).split("\\}")[0];
					String result = "Definition for " + TextFormatting.GRAY + TextFormatting.ITALIC + searchTerm + TextFormatting.RESET + ":\n" + definition + "\n\nExample:\n" + example + "\n\nThumbs up: " + thumbsUp + "\nThumbs down: " + thumbsDown;
					Reference.sendMessage(sender, result);
				}
			} else Reference.sendCommandUsage(sender, usage);
		}
		
		protected String usage = "/urban <search term> Lookup the definition of a word on urban dictionary.";

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