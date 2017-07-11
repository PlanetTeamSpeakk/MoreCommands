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

public class gms {

	public static Object instance;

	public gms() {
	}

	public static class Commandgms extends CommandBase {
		public boolean isUsernameIndex(int var1) {
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

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "gms";
		}

		public String getUsage(ICommandSender var1) {
			return "/gms Puts you in survival mode.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] cmd) {
			EntityPlayer entity = (EntityPlayer) var1;
			
			if (entity instanceof EntityPlayerMP) {
				((EntityPlayer) entity).setGameType(GameType.SURVIVAL);
				var1.sendMessage(new TextComponentString("Your gamemode has been updated to survival mode."));
			}

		}

	}

}