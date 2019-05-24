package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Reference.LogType;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class urban {

	public urban() {
	}

	public static class Commandurban extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("define");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "urban";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				String searchTerm = Reference.join(args);
				String data;
				try {
					data = Reference.getHTML("http://api.urbandictionary.com/v0/define?term=" + searchTerm.replaceAll(" ", "+"));
				} catch (IOException e) {
					Reference.sendMessage(sender, "An unknown error occured while trying to get the data, please try again.");
					e.printStackTrace();
					return;
				}
				if (data.contains("\"result_type\":\"no_results\""))
					Reference.sendMessage(sender, "No results found for the search term " + TextFormatting.GRAY + TextFormatting.ITALIC + searchTerm + TextFormatting.RESET + ".");
				else {
					Gson gson = new Gson();
					Map dataMap = gson.fromJson(data, Map.class);
					List definitions = (List) dataMap.get("list");
					Map definitionsMap = (Map) definitions.toArray()[0];
					Reference.print(LogType.INFO, definitions);
					String definition = Reference.getCleanString((String) definitionsMap.get("definition"));
					String example = Reference.getCleanString((String) definitionsMap.get("example"));
					Long thumbsUp = ((Double) definitionsMap.get("thumbs_up")).longValue();
					Long thumbsDown = ((Double) definitionsMap.get("thumbs_down")).longValue();
					String result = "Definition for " + TextFormatting.GRAY + TextFormatting.ITALIC + searchTerm + TextFormatting.RESET + ":\n" + definition + (!example.equals("") ? "\n\nExample:\n" + example : "") + "\n\nThumbs up: " + thumbsUp + "\nThumbs down: " + thumbsDown;
					Reference.sendMessage(sender, result);
				}
			} else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
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