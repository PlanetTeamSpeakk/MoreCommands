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

public class delHome {

	public delHome() {
	}

	public static class CommanddelHome extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "delhome";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			try {
				if (Reference.doesPlayerHaveHome((EntityPlayer) sender)) {
					Reference.removeHome((EntityPlayer) sender);
					Reference.saveHomesFile();
					Reference.sendMessage(sender, "Your home has been removed.");
				} else Reference.sendMessage(sender, "You do not have a home set.");
			} catch (IOException e) {
				Reference.sendMessage(sender, TextFormatting.RED + "An unknown error occured while attempting to perform this command");
			}

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/delhome Deletes your set home.";

	}

}