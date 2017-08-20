package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class whereAmI {

	public whereAmI() {
	}

	public static class CommandwhereAmI extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("coords");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "whereami";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			Reference.sendMessage(player, "Your coords are \nX: " + player.getPosition().getX() + ", Y: " + player.getPosition().getY() + ", Z: " + player.getPosition().getZ() + 
					"\nChunk: X: " + player.chunkCoordX + ", Y: " + player.chunkCoordY + ", Z: " + player.chunkCoordZ + "\nBiome: " + player.getEntityWorld().getBiome(player.getPosition()).getBiomeName() + ".");
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		protected String usage = "/whereami Shows you your coordinates.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}