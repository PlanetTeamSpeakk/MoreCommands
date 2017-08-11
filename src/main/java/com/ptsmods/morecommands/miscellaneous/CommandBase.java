package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandBase extends net.minecraft.command.CommandBase {
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return null;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

	}
	
	public CommandType getCommandType() {
		return CommandType.UNKNOWN;
	}

}
