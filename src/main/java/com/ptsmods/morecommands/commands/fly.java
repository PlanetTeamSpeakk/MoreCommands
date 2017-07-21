package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class fly {

	public static Object instance;

	public fly() {
	}

	public static class Commandfly extends CommandBase {
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
			return new ArrayList();
		}

		public String getName() {
			return "fly";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0 ) {
				Reference.sendCommandUsage(sender, usage);
			} else if (args.length == 1) {
				if (args[0].equals("on")) {
					EntityPlayer player = (EntityPlayer) sender;
					player.capabilities.allowFlying = true;
					player.capabilities.isFlying = true;
					player.sendPlayerAbilities();
					Reference.sendMessage(player, "Flight mode has been turned on.");
				} else {
					EntityPlayer player = (EntityPlayer) sender;
					player.capabilities.allowFlying = false;
					player.capabilities.isFlying = false;
					player.sendPlayerAbilities();
					Reference.sendMessage(player, "Flight mode has been turned off.");
				}
			} else {
				if (args[0].equals("on")) {
					try {
						EntityPlayer victim = getPlayer(server, sender, args[1]);
						victim.capabilities.allowFlying = true;
						victim.capabilities.isFlying = true;
						victim.sendPlayerAbilities();
						Reference.sendMessage(victim, sender.getName() + " has made you able to fly.");
						Reference.sendMessage(sender, "You made " + victim.getName() + " able to fly.");
					} catch (PlayerNotFoundException e) {
						Reference.sendMessage(sender, "The given player does not exist.");
					}
				} else {
					try {
						EntityPlayer victim = getPlayer(server, sender, args[1]);
						victim.capabilities.allowFlying = false;
						victim.capabilities.isFlying = false;
						victim.sendPlayerAbilities();
						Reference.sendMessage(victim, sender.getName() + " has made you unable to fly.");
						Reference.sendMessage(sender, "You made " + victim.getName() + " unable to fly.");
					} catch (PlayerNotFoundException e) {
						Reference.sendMessage(sender, "The given player does not exist.");
					}
				}
			}


		}
		
		protected String usage = "/fly <on/off> [player]";

	}

}