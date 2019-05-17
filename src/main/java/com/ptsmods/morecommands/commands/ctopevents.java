package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.commands.topevents.Commandtopevents;
import com.ptsmods.morecommands.miscellaneous.ClientEventHandler;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class ctopevents {

	public ctopevents() {}

	public static class Commandctopevents extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
		public String getName() {
			return "ctopevents";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			Commandtopevents.format(sender, ClientEventHandler.eventsfired);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "PERMISSION", "DESCRIPTION", false);
		}

		private String usage = "/ctopevents Shows you the 10 events which are fired most on the client.";

	}

}