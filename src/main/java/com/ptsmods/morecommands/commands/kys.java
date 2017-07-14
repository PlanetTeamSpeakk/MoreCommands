// THIS IS A DUMMY CLASS MEANING IT WON'T BE LOADED INTO THE GAME.
// THIS CLASS IS MEANT TO COPY AND PASTE TO MAKE NEW COMMANDS.

package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class kys {

	public static Object instance;

	public kys() {
	}

	public static class Commandkys extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("suicide");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "kys";
		}

		public String getUsage(ICommandSender sender) {
			return "/kys Commit suicide.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			player.sendMessage(new TextComponentString("Goodbye cruel world."));
			player.setHealth(0F);
			server.sendMessage(new TextComponentString(sender.getName() + " took their own life."));

		}

	}

}