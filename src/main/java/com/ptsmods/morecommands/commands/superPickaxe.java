package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.net.ServerToggleSuperPickaxePacket;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

		public static List<String> enabledFor = new ArrayList();

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (!Reference.isConsole(sender)) {
				String uniqueId = ((EntityPlayer) sender).getUniqueID().toString();
				if (enabledFor.contains(uniqueId)) enabledFor.remove(uniqueId);
				else enabledFor.add(uniqueId);
				Reference.netWrapper.sendTo(new ServerToggleSuperPickaxePacket(enabledFor.contains(uniqueId)), (EntityPlayerMP) sender);
				Reference.sendMessage(sender, "Superpickaxe has been " + (enabledFor.contains(uniqueId) ? "enabled." : "disabled."));
			} else {
				Reference.sendMessage(sender, "This command is not meant for the console.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public boolean singleplayerOnly() {
			return false;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "superpickaxe", "Permission to use the superpickaxe command.", true);
		}

		protected String usage = "/superpickaxe Toggles superpickaxe.";

	}

}