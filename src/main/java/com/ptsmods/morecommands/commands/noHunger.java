package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class noHunger {

	public noHunger() {
	}

	public static class CommandnoHunger extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				ArrayList options = new ArrayList();
				options.add("off");
				options.add("on");
				return options;
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
			return "nohunger";
		}

		public String getUsage(ICommandSender sender) {
			return "/nohunger <on/off> [player] You, or someone else, will never be hungry anymore.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {
				sender.sendMessage(new TextComponentString("§cUsage: /nohunger <on/off> [player] You, or someone else, will never be hungry anymore."));
			} else if (args.length == 1) {
				EntityPlayer player = (EntityPlayer) sender;
				if (args[0].equals("on")) {
					player.getFoodStats().setFoodLevel(20);
					player.getFoodStats().setFoodSaturationLevel(20000000F);
					player.sendMessage(new TextComponentString("You will never be hungry anymore."));
				} else {
					player.getFoodStats().setFoodSaturationLevel(5F);
					player.sendMessage(new TextComponentString("You can get hungry again."));
				}
			} else {
				try {
					EntityPlayer victim = getPlayer(server, sender, args[0]);
					if (args[0].equals("on")) {
						victim.getFoodStats().setFoodLevel(20);
						victim.getFoodStats().setFoodSaturationLevel(20000000F);
						victim.sendMessage(new TextComponentString("You will never be hungry anymore, thanks to " + sender.getName() + "."));
						sender.sendMessage(new TextComponentString(victim.getName() + " will never be hungry anymore."));
					} else {
						victim.getFoodStats().setFoodSaturationLevel(5F);
						victim.sendMessage(new TextComponentString("You can now get hungry again, thanks to " + sender.getName() + "."));
					}
				} catch (PlayerNotFoundException e) {
					sender.sendMessage(new TextComponentString("The given player does not exist."));
					return;
				}
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		@Override
		public boolean singleplayerOnly() {
			return true;
		}

	}

}