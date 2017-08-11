package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import javax.script.ScriptException;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class evalJavaScript {

	public evalJavaScript() {
	}

	public static class CommandevalJavaScript extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "evaljavascript";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				String code = "";
				for (int x = 0; x < args.length; x += 1) {
					code += args[x];
					code += (x+1 != args.length ? " " : "");
				}
				try {
					Reference.sendMessage(sender, "Input: " + TextFormatting.GRAY + TextFormatting.ITALIC + code + TextFormatting.RESET + "\n\nOutput: " + TextFormatting.GRAY + TextFormatting.ITALIC + Reference.evalJavaScript(code) + TextFormatting.RESET);
				} catch (ScriptException e) {
					Reference.sendMessage(sender, "An error occured while evaluating your code.");
				}
			}

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		protected String usage = "/evaljavascript <code> Evaluates JavaScript, idek.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}