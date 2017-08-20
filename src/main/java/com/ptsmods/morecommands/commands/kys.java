package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class kys {

	public kys() {
	}

	public static class Commandkys extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
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
			player.onKillCommand();
			server.sendMessage(new TextComponentString(sender.getName() + " took their own life."));

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}