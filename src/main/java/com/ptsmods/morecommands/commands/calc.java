package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;

public class calc {

	public calc() {
	}

	public static class Commandcalc extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("calculate");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "calc";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				String regex = "(\\d*[\\/\\+\\-\\*\\%]*)"; // sadly, you cannot use brackets in your math equation, so you'd just have to run the command multiple times.
				ClientCommandHandler.instance.executeCommand(sender, "evaljavascript " + Reference.Regex.removeUnwantedChars(regex, Reference.joinCustomChar("", args)));
			} else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		protected String usage = "/calc <equation> Calculates a math equation so you don't have to.";

	}

}