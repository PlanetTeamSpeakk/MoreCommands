package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class day {

	public day() {
	}

	public static class Commandday extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			return new ArrayList();
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "day";
		}

		public String getUsage(ICommandSender sender) {
			return "/day Sets time to day.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			EntityPlayer player = (EntityPlayer) sender;
			Reference.setAllWorldTimes(server, 1000);
			Reference.sendMessage(sender, "The time has been changed to 1000 ticks, aka day-time.");

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

	}

}