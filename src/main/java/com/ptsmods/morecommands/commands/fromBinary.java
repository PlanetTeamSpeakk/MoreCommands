package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.commands.hat.Commandhat;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;

public class fromBinary {

	public fromBinary() {
	}

	public static class CommandfromBinary extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "frombinary";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				String output = "";
				for (String arg : args) {
					try {
						Integer.parseInt(arg, 2);
					} catch (NumberFormatException e) {
						Reference.sendMessage(sender, "One or more characters are not binary.");
						return;
					} catch (NullPointerException e) {}
					output += new Character((char) Integer.parseInt(arg, 2));
				}
				Reference.sendMessage(sender, "" + TextFormatting.GRAY + TextFormatting.ITALIC + Reference.join(args) + TextFormatting.RESET + " = " + TextFormatting.GRAY + TextFormatting.ITALIC + output);
				ClientCommandHandler.instance.executeCommand(sender, "copy " + output);
			}

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		protected String usage = "/frombinary <binary> Converts from binary.";

	}

}