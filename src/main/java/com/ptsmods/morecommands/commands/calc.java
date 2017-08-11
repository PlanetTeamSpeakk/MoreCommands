package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class calc {

	public calc() {
	}

	public static class Commandcalc extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("calculate");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "calc";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				Pattern pattern = Pattern.compile("([-+]?[0-9]*\\.?[0-9]+[\\/\\+\\-\\*\\%])+([-+]?[0-9]*\\.?[0-9]+)");
				for (int x = 0; x < args.length; x += 1) {
					Matcher matcher = pattern.matcher(args[0]);
					if (!matcher.matches()) {
						Reference.sendMessage(sender, "The equation contained characters other than digits and math symbols.");
						return;
					}
				}
				try {
					Reference.sendMessage(sender, args[0] + " = " + Reference.evalJavaScript(args[0]));
				} catch (ScriptException e) {
					Reference.sendMessage(sender, "I can't solve the problem, it's too hard.");
				}
			} else Reference.sendCommandUsage(sender, usage);
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		protected String usage = "/calc <equation> Calculates a math equation so you don't have to.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}
		
	}

}