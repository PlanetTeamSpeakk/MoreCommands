package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;

public class jump {

	public jump() {}

	public static class Commandjump extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "jump";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (sender instanceof Entity) {
				RayTraceResult result = Reference.rayTrace((Entity) sender, 160);
				BlockPos pos = result.entityHit == null ? result.getBlockPos() : result.entityHit.getPosition();
				int xOffset = 0, yOffset = 0, zOffset = 0;
				switch (result.sideHit) {
				case NORTH:
					zOffset = -1;
					break;
				case EAST:
					xOffset = 1;
					break;
				case SOUTH:
					zOffset = 1;
					break;
				case WEST:
					xOffset = -1;
					break;
				case UP:
					yOffset = 1;
					break;
				case DOWN:
					yOffset = -1;
					break;
				}
				((Entity) sender).setPositionAndUpdate(pos.getX() + 0.5D + xOffset, pos.getY() + yOffset, pos.getZ() + 0.5D + zOffset);
			} else Reference.sendMessage(sender, TextFormatting.RED + "You are not an entity in the world!");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "jump", "Teleport to where you're looking.", true);
		}

		private String usage = "/jump Teleport to where you're looking.";

	}

}