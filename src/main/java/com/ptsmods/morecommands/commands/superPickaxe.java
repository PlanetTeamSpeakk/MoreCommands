package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class superPickaxe {

	public superPickaxe() {
	}

	public static class CommandsuperPickaxe extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("/");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "superpickaxe";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		public static Boolean enabled = false;

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			enabled = !enabled;
			Reference.sendMessage(sender, "Superpickaxe has been " + (enabled ? "enabled, do note that using the pickaxe may crash your Minecraft due to some server ticking look randomly giving a ConcurrentModificationException." : "disabled."));

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public boolean singleplayerOnly() {
			return true;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "superpickaxe", "Permission to use the superpickaxe command.", true);
		}

		protected String usage = "/superpickaxe Toggles superpickaxe.";

	}

}