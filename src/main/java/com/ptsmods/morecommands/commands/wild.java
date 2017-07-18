package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import scala.concurrent.forkjoin.ThreadLocalRandom;

public class wild {

	public static Object instance;

	public wild() {
	}

	public static class Commandwild implements ICommand {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

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
			return "wild";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			player.setPosition(ThreadLocalRandom.current().nextDouble(0, 100000), 0, ThreadLocalRandom.current().nextDouble(0, 100000));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			Reference.teleportSafely(player);
			Reference.sendMessage(player, "You have been randomly teleported, your new coords are\nX: " + player.getPosition().getX() + ", Y: " + player.getPosition().getY() + ", Z: " + player.getPosition().getZ() + 
					", Biome: " + player.getEntityWorld().getBiome(new BlockPos(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())).getBiomeName() + ".");

		}
		
		protected String usage = "/wild Teleports you somewhere random.";

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