package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.mojang.text2speech.Narrator;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class narrate {

	public narrate() {
	}

	public static class Commandnarrate extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "narrate";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				if (Minecraft.getMinecraft().getVersion().startsWith("1.11")) Reference.sendMessage(sender, "This command can only be used on 1.12+.");
				else {
					Narrator narrator = Narrator.getNarrator();
					String message = "";
					for (int x = 0; x < args.length; x += 1) {
						message += args[x] + " ";
					}
					narrator.say(message.trim());
				}
			} else {
				Reference.sendMessage(sender, "You can now write a whole novel you want the narrator to say, start typing your messages and once done say 'done' without the quotes, you can also cancel by saying 'cancel' without the quotes. "
						+ TextFormatting.DARK_RED + TextFormatting.BOLD + "The messages won't be sent to the server." + TextFormatting.RESET
						+ " Have fun with the narrator! P.s. if you just want it to say a few words or 1 sentence, you can do /narrate <message>. P.p.s. if you want the narrator to read the whole bee movie script for you type 'bee movie' and then 'done'.");
				Reference.narratorActive = true;
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		protected String usage = "/narrate <message> Narrates something, you can also do /narrate and then type a whole novel you want it to say.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}