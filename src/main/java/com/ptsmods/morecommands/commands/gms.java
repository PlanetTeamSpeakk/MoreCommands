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

	public gms() {
	}

	public static class Commandgms extends CommandBase {
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

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "gms";
		}

		public String getUsage(ICommandSender sender) {
			return "/gms Puts you in survival mode.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			EntityPlayer player = (EntityPlayer) sender;
			player.setGameType(GameType.SURVIVAL);
			sender.sendMessage(new TextComponentString("Your gamemode has been updated to survival mode."));

		}

	}

}