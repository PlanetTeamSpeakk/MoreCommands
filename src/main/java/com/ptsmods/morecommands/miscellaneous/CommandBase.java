package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandBase extends net.minecraft.command.CommandBase {
	
	@Override
	public String getName() {
		return "";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		return;
	}
	
	public CommandType getCommandType() {
		return CommandType.UNKNOWN;
	}
	
	public boolean singleplayerOnly() {
		return false; // make this return true and the command cannot be used on servers
	}
	
	public boolean hasCooldown() {
		return false; // if this returns true the getCooldownSeconds() method will be called to put a cooldown on the command.
	}
	
	public int getCooldownSeconds() {
		return 0;
	}

}
