package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class descend {

	public descend() {
	}

	public static class Commanddescend extends com.ptsmods.morecommands.miscellaneous.CommandBase {
		
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
			return "descend";
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
			for (int x1 = 0; x1 < 256; x1 += 1) { // it will look 256 blocks below you at most.
				y -= 1;
				Block block = world.getBlockState(new BlockPos(x, y-1, z)).getBlock();
				Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				Block tpblock2 = world.getBlockState(new BlockPos(x, y+1, z)).getBlock();
				if (!Reference.getBlockBlacklist().contains(block) && Reference.getBlockWhitelist().contains(tpblock) && Reference.getBlockWhitelist().contains(tpblock2)) {
					player.setPositionAndUpdate(x+0.5, y, z+0.5);
					Reference.sendMessage(player, "You have been teleported through the ground.");
					return;
				}
			}
			// Only got here if no free spot was found.
			Reference.sendMessage(player, "No free spot found below of you.");
			
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/descend Teleport through the ground.";

	}

}