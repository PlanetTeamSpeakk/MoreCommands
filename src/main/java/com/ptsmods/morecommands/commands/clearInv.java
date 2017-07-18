package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class clearInv {

	public static Object instance;

	public clearInv() {
	}

	public static class CommandclearInv implements ICommand {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("clearinv");
			aliases.add("clearinventory");
			aliases.add("cinv");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return (java.util.List) (new java.util.ArrayList());
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "ci";
		}

		public String getUsage(ICommandSender sender) {
			return "/ci Clears your inventory.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException {
			EntityPlayer player = (EntityPlayer) sender;
			player.inventory.clear();
			Reference.sendMessage(sender, "Your inventory was successfully cleared.");
		}

		@Override
		public int compareTo(ICommand o) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}
		
	}

}