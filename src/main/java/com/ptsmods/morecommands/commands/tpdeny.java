package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class tpdeny {

	public static Object instance;

	public tpdeny() {
	}

	public static class Commandtpdeny extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

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
			return "tpdeny";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (Reference.tpRequests.get(sender.getName()) == null) {
				Reference.sendMessage(sender, "You do not have a tpa request open.");
			} else {
				try {
					EntityPlayer requester = getPlayer(server, sender, Reference.tpRequests.get(sender.getName()));
					Reference.sendMessage(requester, sender.getName() + " has denied your tpa request.");
					Reference.tpRequests.remove(sender.getName());
					Reference.sendMessage(sender, "You have denied " + requester.getName() + "'s tpa request.");
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "Error getting the person who tried to teleport to you, are they offline?");
				}
			}

		}
		
		protected String usage = "/tpdeny Deny a tpa request.";

	}

}