package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class kys {

	public kys() {
	}

	public static class Commandkys implements ICommand {

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("suicide");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
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

		@Override
		public int compareTo(ICommand arg0) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			System.out.println("Returned true");
			return true;
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return false;
		}

	}

}