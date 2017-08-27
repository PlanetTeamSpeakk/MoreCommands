package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class delWarp {

	public delWarp() {
	}

	public static class CommanddelHome extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
		public String getName() {
			return "delwarp";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0)
				try {
					if (Reference.doesWarpExist(args[0])) {
						Reference.removeHome((EntityPlayer) sender);
						Reference.saveHomesFile();
						Reference.sendMessage(sender, "The warp has been removed.");
					} else Reference.sendMessage(sender, "That warp does not exist.");
				} catch (IOException e) {
					Reference.sendMessage(sender, TextFormatting.RED + "An unknown error occured while attempting to perform this command");
				}
			else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "delwarp", "Permission to use the delwarp command.", true);
		}

		protected String usage = "/delwarp <name> Deletes a warp.";

	}

}