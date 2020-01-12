package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class descend {

	public descend() {}

	public static class Commanddescend extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "descend";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (sender instanceof Entity) {
				Entity entity = (Entity) sender;
				BlockPos pos = entity.getPosition();
				World world = entity.getEntityWorld();
				Integer x = pos.getX();
				Integer y = pos.getY() - 2;
				Integer z = pos.getZ();
				for (; y > 0; y--) {
					Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
					Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock();
					if (!Reference.blockBlacklist.contains(block) && Reference.blockWhitelist.contains(tpblock) && Reference.blockWhitelist.contains(tpblock2)) {
						entity.setPositionAndUpdate(x + 0.5, y, z + 0.5);
						Reference.sendMessage(entity, "You have been teleported through the ground.");
						return;
					}
				}
				// Only got here if no free spot was found.
				Reference.sendMessage(entity, "No free spot found below of you.");
			} else Reference.sendMessage(sender, TextFormatting.RED + "Only entities may use this command.");

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "descend", "Permission to use the descend command.", true);
		}

		protected String usage = "/descend Teleport through the ground.";

	}

}