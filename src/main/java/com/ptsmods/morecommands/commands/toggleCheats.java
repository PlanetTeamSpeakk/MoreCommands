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

public class toggleCheats {

	public toggleCheats() {}

	public static class CommandtoggleCheats extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "togglecheats";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			Lists.newArrayList(server.worlds[0]).forEach(world -> world.getWorldInfo().setAllowCommands(!world.getWorldInfo().areCommandsAllowed()));
			Reference.sendMessage(sender, "Cheats have been", Reference.getColorFromBoolean(sender.getEntityWorld().getWorldInfo().areCommandsAllowed()) + (sender.getEntityWorld().getWorldInfo().areCommandsAllowed() ? "enabled" : "disabled") + Reference.dtf + ".");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "PERMISSION", "DESCRIPTION", false);
		}

		private String usage = "/togglecheats Turn cheats either on or off.";

		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return server.isSinglePlayer() && server.getServerOwner().equals(sender.getName());
		}

	}

}