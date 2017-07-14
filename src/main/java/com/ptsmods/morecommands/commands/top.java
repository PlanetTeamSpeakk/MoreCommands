package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class top {

	public static Object instance;

	public top() {
	}

	private static ArrayList blockBlacklist = new ArrayList();
	
	public static ArrayList getBlockBlacklist() {
		return blockBlacklist;
	}

	public static boolean addBlockToBlacklist(Block block) {
		return blockBlacklist.add(block);
	}
	
	public static void resetBlockBlackAndWhitelist() {
		blockBlacklist = new ArrayList();
		blockWhitelist = new ArrayList();
	}
	
	private static ArrayList blockWhitelist = new ArrayList();
	
	public static ArrayList getBlockWhitelist() {
		return blockWhitelist;
	}

	public static boolean addBlockToWhitelist(Block block) {
		return blockWhitelist.add(block);
	}
	
	public static class Commandtop extends CommandBase {
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

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "top";
		}

		public String getUsage(ICommandSender sender) {
			return this.usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			World world = player.getEntityWorld();
			float x = player.getPosition().getX();
			float z = player.getPosition().getZ();
			boolean found = false;
			if (!world.isRemote) {
				while (!found) {
					for (Integer y = 256; y != player.getPosition().getY(); y -= 1) {
						Block block = world.getBlockState(new BlockPos(x, y-1, z)).getBlock();
						Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
						if (!getBlockBlacklist().contains(block) && getBlockWhitelist().contains(tpblock)) {
							player.setPositionAndUpdate(x+0.5, y, z+0.5);
							found = true;
							Reference.sendMessage(player, "You have been teleported to a safe location.");
							break;
						}
					}
					x -= 1;
					z -= 1;
				}
			}

		}
		
		protected String usage = "/top Teleports you to a safe destination.";
		
	}

}