package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class setFOV {

	public static Object instance;

	public setFOV() {
	}

	public static class CommandsetFOV implements ICommand{
		public boolean isUsernameIndex(int sender) {
			return false;
		}

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("sfov");
			aliases.add("setfieldofview");
			aliases.add("setfieldofvision"); // it's field of view, but some people call it field of vision.
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "setfov";
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
				Minecraft.getMinecraft().gameSettings.fovSetting = Integer.parseInt(args[0]);
				Minecraft.getMinecraft().gameSettings.renderDistanceChunks = 64;
				Minecraft.getMinecraft().gameSettings.saveOptions();
				Reference.sendMessage(sender, "Your fov has been set to " + Float.toString(Minecraft.getMinecraft().gameSettings.fovSetting) + ".");
			}
		}

		private static String usage = "/setfov <fov> Changes your fov, normal fov is 70.";
		
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