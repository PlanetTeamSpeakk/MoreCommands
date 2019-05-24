package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.ServerEventHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class topevents {

	public topevents() {}

	public static class Commandtopevents extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "topevents";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			format(sender, ServerEventHandler.eventsfired);
		}

		public static void format(ICommandSender sender, Map<String, Long> table) {
			Map<String, Long> entries = new HashMap<>(table).entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
			List<String> entriesKeys = new ArrayList(entries.keySet());
			String output = "The top 10 most fired events are:\n";
			for (int i = 0; i < 10 && i < entries.size(); i++)
				output += TextFormatting.YELLOW + "" + (i + 1) + ". " + TextFormatting.GOLD + entriesKeys.get(i).split("\\.")[entriesKeys.get(i).split("\\.").length - 1] + " fired " + TextFormatting.YELLOW + entries.get(entriesKeys.get(i)) + TextFormatting.GOLD + " times.\n";
			Reference.sendMessage(sender, output.trim());
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "topevents", "Shows you the 10 events which are fired most on the server.", true);
		}

		private String usage = "/topevents Shows you the 10 events which are fired most on the server.";

	}

}