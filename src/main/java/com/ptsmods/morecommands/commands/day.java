package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class day {

	public day() {
	}

	public static class Commandday extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			return new ArrayList();
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "day";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/day Sets time to day.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			Reference.setAllWorldTimes(server, 1000);
			Reference.sendMessage(sender, "The time has been changed to 1000 ticks, aka day-time.");

		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "day", "Permission to use the day command.", true);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

	}

}