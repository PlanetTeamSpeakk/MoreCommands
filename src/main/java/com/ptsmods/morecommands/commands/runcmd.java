package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class runcmd {

	public runcmd() {
	}

	public static class Commandruncmd extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "runcmd";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			server.getCommandManager().executeCommand(sender, Reference.join(args));
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		protected String usage = "/runcmd <command> Runs a server command client side, idk why you would want this.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}