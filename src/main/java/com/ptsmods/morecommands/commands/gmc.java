package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;

public class gmc {

	public static Object instance;

	public gmc() {
	}

	public static class Commandgmc extends CommandBase {
		public boolean isUsernameIndex(int var1) {
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

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "gmc";
		}

		public String getUsage(ICommandSender var1) {
			return "/gmc Puts you in creative mode.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] cmd) {
			EntityPlayer entity = (EntityPlayer) var1;
			
			if (entity instanceof EntityPlayerMP) {
				((EntityPlayer) entity).setGameType(GameType.CREATIVE);
				var1.sendMessage(new TextComponentString("Your gamemode has been updated to creative mode."));
			}

		}

	}

}