package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class clearInv {

	public static Object instance;

	public clearInv() {
	}

	public static class CommandclearInv extends CommandBase {
		public boolean isUsernameIndex(int var1) {
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

		public String getUsage(ICommandSender var1) {
			return "/ci Clears your inventory.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] args) throws NumberInvalidException {
			EntityPlayer entity = (EntityPlayer) var1;
						
			if (entity instanceof EntityPlayer) {
				((EntityPlayer) entity).inventory.clear();
				var1.sendMessage(new TextComponentString("Your inventory was successfully cleared."));
			}
		}
		
	}

}