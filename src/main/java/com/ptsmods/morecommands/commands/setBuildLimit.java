package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class setBuildLimit {

	public setBuildLimit() {
	}

	public static class CommandsetBuildLimit extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
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
			return "setbuildlimit";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0)
				Reference.sendCommandUsage(sender, usage);
			else if (Integer.parseInt(args[0]) > 256 || Integer.parseInt(args[0]) < 0)
				Reference.sendMessage(sender, "The limit should be anything between 0 and 256.");
			else {
				Integer limit = Integer.parseInt(args[0]);
				server.setBuildLimit(limit);
				Reference.sendMessage(sender, "The build limit has been set to " + limit.toString() + ".");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "sbl", "Permission to use the setbuildlimit command.", true);
		}

		protected String usage = "/setbuildlimit <limit>";

	}

}