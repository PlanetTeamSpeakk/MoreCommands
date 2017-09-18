package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import javax.script.ScriptException;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class evalJavaScript {

	public evalJavaScript() {
	}

	public static class CommandevalJavaScript extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "evaljavascript";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else
				new Thread(new Runnable() {
					@Override
					public void run() {
						String code = Reference.join(args);
						try {
							Reference.sendMessage(sender, "Input: " + TextFormatting.GRAY + TextFormatting.ITALIC + code + TextFormatting.RESET + "\n\nOutput: " + TextFormatting.GRAY + TextFormatting.ITALIC + Reference.evalJavaScript(code) + TextFormatting.RESET);
						} catch (ScriptException e) {
							Reference.sendMessage(sender, "An error occured while evaluating your code.");
						}
					}
				}).start();
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