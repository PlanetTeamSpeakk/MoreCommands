package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class through {

	public through() {
	}

	public static class Commandthrough extends CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("thru");
			aliases.add("trhough"); // all the typos
			aliases.add("though");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "through";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			BlockPos playerpos = player.getPosition();
			World world = player.getEntityWorld();
			Integer x = playerpos.getX();
			Integer y = playerpos.getY();
			Integer z = playerpos.getZ();
			String direction = Reference.getLookDirectionFromLookVec(player.getLookVec());
			for (int x1 = 0; x1 < 256; x1 += 1) { // it will look 256 blocks in front of you at most.
				if (direction.equals("north")) {
					z -= 1;
				} else if (direction.equals("west")) {
					x -= 1;
				} else if (direction.equals("south")) {
					z += 1;
				} else if (direction.equals("east")) {
					x += 1;
				} else if (direction.equals("down")) {
					y -= 1;
				} else if (direction.equals("up")) {
					y += 1;
				} else if (direction.equals("unknown")) {
					Reference.sendMessage(player, "The direction your facing could not be determined.");
					return;
				} else {
					Reference.sendMessage(player, "Someone messed with the code, the returned direction wasn't north, west, south, east, down, up or unknown.");
					return;
				}
				Block block = world.getBlockState(new BlockPos(x, y-1, z)).getBlock();
				Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				Block tpblock2 = world.getBlockState(new BlockPos(x, y+1, z)).getBlock();
				if (!Reference.getBlockBlacklist().contains(block) && Reference.getBlockWhitelist().contains(tpblock) && Reference.getBlockWhitelist().contains(tpblock2)) {
					player.setPositionAndUpdate(x+0.5, y, z+0.5);
					Reference.sendMessage(player, "You have been teleported through the wall.");
					return;
				}
			}
			// Only got here if no free spot was found.
			Reference.sendMessage(player, "No free spot found ahead of you.");
			
		}
		
		protected String usage = "/through Teleport through the wall.";

	}

}