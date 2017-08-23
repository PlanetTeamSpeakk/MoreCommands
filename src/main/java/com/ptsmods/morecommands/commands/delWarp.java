package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
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

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "delwarp";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				try {
					if (Reference.doesWarpExist(args[0])) {
						Reference.removeHome((EntityPlayer) sender);
						Reference.saveHomesFile();
						Reference.sendMessage(sender, "The warp has been removed.");
					} else Reference.sendMessage(sender, "That warp does not exist.");
				} catch (IOException e) {
					Reference.sendMessage(sender, TextFormatting.RED + "An unknown error occured while attempting to perform this command");
				}
			} else Reference.sendCommandUsage(sender, usage);
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/delwarp <name> Deletes a warp.";

	}

}