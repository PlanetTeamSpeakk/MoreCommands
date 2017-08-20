package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;

public class toBinary {

	public toBinary() {
	}

	public static class CommandtoBinary extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "tobinary";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				String text = Reference.join(args);
				byte[] bytes = text.getBytes();
				String output = "";
				for (byte b : bytes) {
					int val = b;
					for (int x = 0; x < 8; x++) {
						output += (val & 128) == 0 ? 0 : 1;
						val <<= 1;
					}
					output += " ";
				}
				Reference.sendMessage(sender, "" + TextFormatting.GRAY + TextFormatting.ITALIC + text + TextFormatting.RESET + " = " + TextFormatting.GRAY + TextFormatting.ITALIC + output);
				ClientCommandHandler.instance.executeCommand(sender, "copy " + output);
			} else Reference.sendCommandUsage(sender, usage);

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		protected String usage = "/tobinary <string> Converts a string to binary, idfk why.";

	}

}