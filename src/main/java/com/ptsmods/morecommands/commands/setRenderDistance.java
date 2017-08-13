package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class setRenderDistance {

	public setRenderDistance() {
	}

	public static class CommandsetRenderDistance extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("srd");
			aliases.add("setviewdistance"); // it's render distance, but some people call it view distance.
			aliases.add("svd");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "setrenderdistance";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) {
				Reference.sendCommandUsage(sender, usage);
			} else if (!Reference.isInteger(args[0])) {
				Reference.sendCommandUsage(sender, usage);
			} else {
				Minecraft.getMinecraft().gameSettings.renderDistanceChunks = Integer.parseInt(args[0]);
				Minecraft.getMinecraft().gameSettings.saveOptions();
				Reference.sendMessage(sender, "Your render distance has been set to " + Integer.toString(Minecraft.getMinecraft().gameSettings.renderDistanceChunks) + ".");
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		private static String usage = "/setrenderdistance <distance> Sets your render distance to the given amount, 12 is recommended.";
		
		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}