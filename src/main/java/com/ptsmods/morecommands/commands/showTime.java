package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class showTime {

	public static Object instance;

	public showTime() {
	}

	public static class CommandshowTime extends CommandBase {
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

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "showtime";
		}

		public String getUsage(ICommandSender sender) {
			return "/showtime Shows the time";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			Integer time = (int)(sender.getEntityWorld().getWorldTime() % 24000L);
			sender.sendMessage(new TextComponentString("The server time is " + time.toString() + " ticks, " + Reference.parseTime(time, true) + " or " + Reference.parseTime(time, false) + "."));
		}

	}

}