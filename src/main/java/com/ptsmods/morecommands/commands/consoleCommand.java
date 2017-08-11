package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class consoleCommand {

	public consoleCommand() {
	}

	public static class CommandconsoleCommand extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("ccmd");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "consolecommand";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) {
				Reference.sendCommandUsage(sender, usage);
			} else {
				String command = "";
				for (Integer length = 0; length < args.length; length += 1) {
					command += args[length];
					if ((length + 1) != args.length) command += " ";
				}
				server.getCommandManager().executeCommand((ICommandSender) server, command);
				Reference.sendMessage(sender, "Successfully ran command " + TextFormatting.GRAY + TextFormatting.ITALIC + command + TextFormatting.RESET + " from the console. Some commands may give errors, or not work at all when ran by the console.");
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/consolecommand <command> Runs a command from the console.";

	}

}