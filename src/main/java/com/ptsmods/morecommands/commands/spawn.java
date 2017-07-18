package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class spawn {

	public static Object instance;

	public spawn() {
	}

	public static class Commandspawn implements ICommand {

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "spawn";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			World world = player.getEntityWorld();
			BlockPos spawnpoint = world.getSpawnPoint();
			player.setPositionAndUpdate(spawnpoint.getX(), spawnpoint.getY(), spawnpoint.getZ());
			Reference.teleportSafely(player);
			Reference.sendMessage(player, "You have been teleported to spawn.");

		}
		
		protected String usage = "/spawn Teleports you to spawn.";

		@Override
		public int compareTo(ICommand o) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return false;
		}

	}

}