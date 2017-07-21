package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class setBuildLimit {

	public static Object instance;

	public setBuildLimit() {
	}

	public static class CommandsetBuildLimit extends CommandBase {
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
			return "setbuildlimit";
		}

		public String getUsage(ICommandSender sender) {
			return this.usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) {
				Reference.sendCommandUsage(sender, usage);
			} else if (Integer.parseInt(args[0]) > 256 || Integer.parseInt(args[0]) < 0) {
				Reference.sendMessage(sender, "The limit should be anything between 0 and 256.");
			}
			else {
				Integer limit = Integer.parseInt(args[0]);
				server.setBuildLimit(limit);
				Reference.sendMessage(sender, "The build limit has been set to " + limit.toString() + ".");
			}

		}
		
		protected String usage = "/setbuildlimit <limit>";

	}

}