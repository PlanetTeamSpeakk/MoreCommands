package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class silence {

	public silence() {}

	public static class Commandsilence extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("quiet");
			aliases.add("s");
			aliases.add("q");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "silence";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (sender instanceof Entity) {
				if (args.length != 0 && args[0].equals("get")) sender.sendMessage(new TextComponentString(Reference.dtf + "You have " + (Reference.silenced.contains(((Entity) sender).getUniqueID()) ? "" : "not ") + "silenced MoreCommands."));
				else if (Reference.silenced.contains(((Entity) sender).getUniqueID())) Reference.silenced.remove(((Entity) sender).getUniqueID());
				else Reference.silenced.add(((Entity) sender).getUniqueID());
			} else Reference.sendMessage(sender, "This command may only be used by entities.");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "PERMISSION", "DESCRIPTION", false);
		}

		private String usage = "/silence [get] Silences all messages you receive from MoreCommands commands or gets if you have silenced MoreCommands. To get if you have silenced MoreCommands, type /silence get. Useful for macros.";

	}

}