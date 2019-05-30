package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;

public class gmsp {

	public static Object instance;

	public gmsp() {}

	public static class Commandgmsp extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

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
			return "gmsp";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/gmsp Puts you in spectator mode.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			player.setGameType(GameType.SPECTATOR);
			Reference.sendMessage(sender, "Your gamemode has been updated to spectator mode.");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "gmsp", "Permission to use the gmsp command.", true);
		}

	}

}