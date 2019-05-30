package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class calc {

	public calc() {
	}

	public static class Commandcalc extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("calculate");
			aliases.add("cacl");
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
				String equation = Reference.joinCustomChar("", args);
				try {
					Reference.sendMessage(sender, equation + " = " + Reference.formatDouble(Reference.eval(equation)));
				} catch (RuntimeException e) {
					Reference.sendMessage(sender, e.getMessage());
				}
			} else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		protected String usage = "/calc <equation> Calculates a math equation so you don't have to. It does addition, subtraction, multiplication, division, exponentiation (using the ^ symbol), factorial (! before a number), and features like cos(int), tan(int), sin(int), pi(int), sqrt(int) and cbrt(int) in which int is replaced with a number.";

	}

}