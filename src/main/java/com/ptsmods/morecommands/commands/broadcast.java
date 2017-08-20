package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.commands.hat.Commandhat;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class broadcast {

	public broadcast() {
	}

	public static class Commandbroadcast extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "broadcast";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				String message = Reference.convertColorCodes(Reference.join(args));
				Reference.sendServerMessage(server, message);
			} else Reference.sendCommandUsage(sender, usage);

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/broadcast <message> Broadcasts a message to the whole server, you can use color codes e.g. &6, &4.";

	}

}