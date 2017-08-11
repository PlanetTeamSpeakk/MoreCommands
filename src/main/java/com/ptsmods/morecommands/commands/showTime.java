package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class showTime {

	public showTime() {
	}

	public static class CommandshowTime extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			Reference.sendMessage(sender, "The server time is " + time.toString() + " ticks, " + Reference.parseTime(time, true) + " or " + Reference.parseTime(time, false) + ".");
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