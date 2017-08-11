package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;

public class gmsp {

	public static Object instance;

	public gmsp() {
	}

	public static class Commandgmsp extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
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
			return "gmsp";
		}

		public String getUsage(ICommandSender sender) {
			return "/gmsp Puts you in spectator mode.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			EntityPlayer player = (EntityPlayer) sender;
			player.setGameType(GameType.SPECTATOR);
			sender.sendMessage(new TextComponentString("Your gamemode has been updated to spectator mode."));

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

	}

}