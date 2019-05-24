// THIS IS A DUMMY CLASS MEANING IT WON'T BE LOADED INTO THE GAME.
// THIS CLASS IS MEANT TO COPY AND PASTE TO MAKE NEW COMMANDS.

package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class dummyCommand {

	public dummyCommand() {}

	public static class CommandCOMMANDNAME extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public List getAliases() {
			return Lists.newArrayList();
		}

		@Override
		public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "COMMAND NAME";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			// CODE HERE

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.UNKNOWN;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "PERMISSION", "DESCRIPTION", true);
		}

		private String usage = "COMMAND USAGE";

	}

}