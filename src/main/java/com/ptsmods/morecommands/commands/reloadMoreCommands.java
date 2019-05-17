package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.mojang.util.UUIDTypeAdapter;
import com.ptsmods.morecommands.Initialize;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class reloadMoreCommands {

	public reloadMoreCommands() {}

	public static class CommandreloadMoreCommands extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("rmc");
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
			return "reloadmorecommands";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (sender instanceof DedicatedServer || sender instanceof Entity && ((Entity) sender).getUniqueID().equals(UUIDTypeAdapter.fromString("1aa35f31-0881-4959-bd14-21e8a72ba0c1"))) {
				Reference.resetBlockBlackAndWhitelist();
				Reference.resetCommandRegistry(CommandType.CLIENT);
				Reference.resetCommandRegistry(CommandType.SERVER);
				Initialize.setupCommandRegistry();
				Initialize.registerCommands(server);
				Reference.sendMessage(sender, "MoreCommands commands have successfully been reloaded.");
			} else Reference.sendMessage(sender, TextFormatting.RED + "You do not have permission to use this, only PlanetTeamSpeak and the console may use this.");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "rmc", "Permission to use the reloadmorecommands command.", false);
		}

		protected String usage = "/reloadmorecommands Reloads all MoreCommands commands, only PlanetTeamSpeak and the console may use this.";

	}

}