package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Reference.Random;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;

public class village {

	public village() {}

	public static class Commandvillage extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("vil");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "village";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else if (args[0].equals("tp")) {
				List<BlockPos> positions = new ArrayList();
				for (Village village : sender.getEntityWorld().getVillageCollection().getVillageList())
					if (village.getNearestDoor(sender.getPosition()) == null) continue;
					else positions.add(village.getNearestDoor(sender.getPosition()).getDoorBlockPos());
				if (positions.isEmpty()) Reference.sendMessage(sender, "This world does not have any villages loaded right now.");
				else if (args.length > 1 && args[0].equals("nearest")) {
					BlockPos pos = sender.getPosition();
					BlockPos nearest = positions.get(0);
					for (BlockPos pos0 : positions)
						if (pos0.getDistance(pos.getX(), pos.getY(), pos.getZ()) < nearest.getDistance(pos.getX(), pos.getY(), pos.getZ())) nearest = pos0;
					getCommandSenderAsPlayer(sender).setPositionAndUpdate(nearest.getX(), nearest.getY(), nearest.getZ());
					Reference.teleportSafely(getCommandSenderAsPlayer(sender));
					Reference.sendMessage(sender, "You have been teleported to the nearest village.");
				} else {
					BlockPos pos = positions.get(Random.randInt(positions.size()));
					getCommandSenderAsPlayer(sender).setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
					Reference.teleportSafely(getCommandSenderAsPlayer(sender));
					Reference.sendMessage(sender, "You have been teleported to a random village.");
				}
			} else if (args[0].equals("list")) {
				List<String> positions = new ArrayList();
				for (Village village : sender.getEntityWorld().getVillageCollection().getVillageList()) {
					if (village.getNearestDoor(sender.getPosition()) == null) continue;
					BlockPos pos = village.getNearestDoor(sender.getPosition()).getDoorBlockPos();
					positions.add("X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ());
				}
				Reference.sendMessage(sender, positions.isEmpty() ? "No villages were found." : "The following coordinates were found: " + Reference.joinCustomChar("; ", positions.toArray(new String[0])));
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "village", "Teleport to the nearest village!", true);
		}

		private String usage = "/village <list:tp> [arg] List this worlds villages or teleport to either the nearest or a random one.";

	}

}