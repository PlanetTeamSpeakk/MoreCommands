package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class tpChunk {

	public tpChunk() {
	}

	public static class CommandtpChunk extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "tpchunk";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 3 || !Reference.isLong(args[0]) || !Reference.isInteger(args[1]) || !Reference.isInteger(args[2])) Reference.sendCommandUsage(sender, usage);
			else {
				EntityPlayer player = (EntityPlayer) sender;
				Long chunkX = Long.parseLong(args[0]);
				Long chunkY = Long.parseLong(args[1]);
				Long chunkZ = Long.parseLong(args[2]);
				player.setPositionAndUpdate((chunkX*16)+8, chunkY*16, (chunkZ*16)+8);
				Reference.sendMessage(sender, "You have been teleported.");
			}

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return super.checkPermission(server, sender);
			//return true;
		}
		
		protected String usage = "/tpchunk <x> <y> <z> Teleports you to the given chunk.";

	}

}