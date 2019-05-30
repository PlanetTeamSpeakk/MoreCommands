package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class through {

	public through() {}

	public static class Commandthrough extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("thru");
			aliases.add("trhough"); // all the typos
			aliases.add("though");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "through";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			BlockPos playerpos = player.getPosition();
			World world = player.getEntityWorld();
			Integer x = playerpos.getX();
			Integer y = playerpos.getY();
			Integer z = playerpos.getZ();
			String direction = Reference.getLookDirectionFromLookVec(player.getLookVec());
			Boolean found = false;
			boolean yLowered = false;
			while (!found) {
				for (int x1 = 0; x1 < 64; x1++) { // it will look 64 blocks in front of you at most.
					if (direction.equals("north")) z--;
					else if (direction.equals("west")) x--;
					else if (direction.equals("south")) z++;
					else if (direction.equals("east")) x++;
					else if (direction.equals("down")) {
						server.getCommandManager().getCommands().get("descend").execute(server, sender, args);
						return;
					} else if (direction.equals("up")) {
						server.getCommandManager().getCommands().get("ascend").execute(server, sender, args);
						return;
					} else if (direction.equals("unknown")) {
						Reference.sendMessage(player, "The direction your facing could not be determined.");
						return;
					} else {
						Reference.sendMessage(player, "Someone messed with the code, the returned direction wasn't north, west, south, east, down, up or unknown.");
						return;
					}
					Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock(); // Block under your feet.
					Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock(); // Block at your feet.
					Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock(); // Block at your head.
					if ((!Reference.blockBlacklist.contains(block) || player.capabilities.isFlying) && (Reference.blockWhitelist.contains(tpblock) || tpblock == Blocks.AIR && player.capabilities.isFlying) && (Reference.blockWhitelist.contains(tpblock2) || tpblock2 == Blocks.AIR && player.capabilities.isFlying)) {
						player.setPositionAndUpdate(x + player.posX - (int) player.posX, y, z + player.posZ - (int) player.posZ);
						Reference.sendMessage(player, "You have been teleported through the wall.");
						found = true;
						return;
					}
				}
				if (y <= playerpos.getY() && y != playerpos.getY() - 8 && !yLowered) {
					y -= 1;
					x = playerpos.getX();
					z = playerpos.getZ();
				} else if (y == playerpos.getY() - 8 && y != playerpos.getY() + 8) {
					yLowered = true;
					y += 1;
					x = playerpos.getX();
					z = playerpos.getZ();
				}
			}
			// Only got here if no free spot was found.
			Reference.sendMessage(player, "No free spot found ahead of you.");

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "through", "Teleport through the wall.", true);
		}

		protected String usage = "/through Teleport through the wall.";

	}

}