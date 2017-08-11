package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class killAll {

	public killAll() {
	}

	public static class CommandkillAll extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

		public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			return new ArrayList();
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
			} else {
				return new ArrayList();
			}
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "killall";
		}

		public String getUsage(ICommandSender sender) {
			return this.usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			if (args.length == 1) {
				if (args[0].equals("all")) args[0] = "!player";
				boolean sendCommandFeedback = player.getEntityWorld().getGameRules().getBoolean("sendCommandFeedback");
				player.getEntityWorld().getGameRules().setOrCreateGameRule("sendCommandFeedback", "false");
				server.getCommandManager().executeCommand((ICommandSender) server, "tp @e[type=" + args[0] + "] ~ -128 ~");
				server.getCommandManager().executeCommand((ICommandSender) server, "kill @e[type=" + args[0] + "]");
				player.getEntityWorld().getGameRules().setOrCreateGameRule("sendCommandFeedback", Boolean.toString(sendCommandFeedback));
				Reference.sendMessage(player, "Successfully killed all entities of type " + args[0] + ".");
			} else {
				Reference.sendCommandUsage(sender, usage);
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/killall <entity> Kills all of the given entity in the world.";

	}

}