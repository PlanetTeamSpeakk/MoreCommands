package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class clearInv {

	public static Object instance;

	public clearInv() {
	}

	public static class CommandclearInv extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public boolean isUsernameIndex(int sender) {
			return false;
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
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}
		
	}

}