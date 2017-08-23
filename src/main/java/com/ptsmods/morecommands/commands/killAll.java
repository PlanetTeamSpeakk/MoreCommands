package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.EntitySelector;
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
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 1) {
				int counter = 0;
				if (args[0].equals("all")) args[0] = "!player";
				for (Entity entity : EntitySelector.matchEntities(sender, "@e[type=" + args[0] + "]", Entity.class)) {
					entity.setPositionAndUpdate(entity.posX, -128, entity.posZ);
					entity.onKillCommand();
					counter += 1;
				}
				Reference.sendMessage(sender, "Successfully killed all entities of type " + args[0] + ", killing a total of " + counter + " entities.");
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