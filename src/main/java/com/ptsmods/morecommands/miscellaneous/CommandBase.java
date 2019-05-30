package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class CommandBase extends net.minecraft.command.CommandBase {

	@Override
	public abstract String getName();

	@Override
	public abstract String getUsage(ICommandSender sender);

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public abstract void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

	public abstract CommandType getCommandType(); // UNKNOWN and it won't be registered, CLIENT and it will be registered Client
													// Side or SERVER and it will be registered Server Side.

	public boolean singleplayerOnly() {
		return false; // make this return true and the command cannot be used on servers
	}

	public boolean hasCooldown() {
		return false; // if this returns true the getCooldownSeconds() method will be called to put a
						// cooldown on the command.
	}

	public int getCooldownSeconds() {
		return 0;
	}

	public Permission getPermission() { // this has to be set to something if the command is server sided, if the player
										// has this permission in any group the player has, the player will be able to
										// use this command.
		return new Permission(null, null, null, false);
	}

	public boolean runOnTicker() {
		return true;
	}

}
