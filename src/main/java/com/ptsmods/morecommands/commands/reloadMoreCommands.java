// THIS IS A DUMMY CLASS MEANING IT WON'T BE LOADED INTO THE GAME.
// THIS CLASS IS MEANT TO COPY AND PASTE TO MAKE NEW COMMANDS.

package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Initialize;
import com.ptsmods.morecommands.Reference;
import com.ptsmods.morecommands.commands.top.Commandtop;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class reloadMoreCommands {

	public static Object instance;

	public reloadMoreCommands() {
	}

	public static class CommandreloadMoreCommands implements ICommand {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("rmc");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "reloadmorecommands";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			top.resetBlockBlackAndWhitelist();
			Initialize.register(Reference.getServerStartingEvent());
			Reference.sendMessage(sender, "MoreCommands commands have successfully been reloaded.");

		}
		
		protected String usage = "/reloadmorecommands Reloads all MoreCommands commands, only PlanetTeamSpeak and Forge Test Environment accounts may use this.";

		@Override
		public int compareTo(ICommand arg0) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			if ((sender.getName().startsWith("Player") && sender.getName().length() == 9) || sender.getName().equals("PlanetTeamSpeak")) return true;
			return false;
		}

	}

}