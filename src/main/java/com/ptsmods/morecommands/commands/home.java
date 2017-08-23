package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class home {

	public home() {
	}

	public static class Commandhome extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "home";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			try {
				Map<String, Double> data = new HashMap<String, Double>();
				boolean isOwnHome = true;
				if (!Reference.isHomesFileLoaded()) Reference.loadHomesFile();
				if (args.length != 0 && Reference.isOp((EntityPlayer) sender)) {
					if (Reference.homes.containsKey(args[0])) {
						data = Reference.homes.get(args[0]);
						isOwnHome = false;
					} else {
						Reference.sendMessage(sender, "The given player does not have a home.");
						return;
					}
				} else {
					if (Reference.doesPlayerHaveHome((EntityPlayer) sender)) data = Reference.homes.get(sender.getName());
					else {
						Reference.sendMessage(sender, "You do not have a home set.");
						return;
					}
				}
				EntityPlayer player = (EntityPlayer) sender;
				player.setPositionAndRotation(0, 0, 0, Reference.doubleToFloat(data.get("yaw")), Reference.doubleToFloat(data.get("pitch")));
		        player.setPositionAndUpdate(data.get("x"), data.get("y"), data.get("z"));
				if (isOwnHome) Reference.sendMessage(sender, "You have been teleported to your home.");
				else Reference.sendMessage(sender, "You have been teleported to " + args[0] + "'s home.");
			} catch (IOException e) {
				Reference.sendMessage(sender, TextFormatting.RED + "An unknown error occured while attempting to perform this command");
			}

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/home Teleports you to your home.";

	}

}