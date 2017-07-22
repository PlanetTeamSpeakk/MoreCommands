package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class fullbright {

	public static Object instance;

	public fullbright() {
	}

	public static class Commandfullbright implements ICommand{
		public boolean isUsernameIndex(int sender) {
			return false;
		}

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("fb");
			aliases.add("nightvision");
			aliases.add("nv");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "fullbright";
		}

		public String getUsage(ICommandSender sender) {
			return "/fullbright Makes your screen bright.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			Minecraft.getMinecraft().gameSettings.gammaSetting = 1000;
			Minecraft.getMinecraft().gameSettings.saveOptions();
			Reference.sendMessage(sender, "Now you can see anything! To remove the effect set your gamma setting to something different.");
		}

		@Override
		public int compareTo(ICommand arg0) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}