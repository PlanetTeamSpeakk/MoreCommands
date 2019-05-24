package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.WarpsHelper;
import com.ptsmods.morecommands.miscellaneous.WarpsHelper.Warp;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class warp {

	public warp() {}

	public static class Commandwarp extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return args.length == 1 ? getListOfStringsMatchingLastWord(args, WarpsHelper.getWarpNames(sender.getEntityWorld())) : new ArrayList();
		}

		@Override
		public String getName() {
			return "warp";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length != 0) if (WarpsHelper.doesWarpExist(sender.getEntityWorld(), args[0])) {
				Warp warp = WarpsHelper.getWarpByName(sender.getEntityWorld(), args[0]);
				EntityPlayer player = (EntityPlayer) sender;
				if (player.dimension != warp.dimension) server.getPlayerList().transferPlayerToDimension(getCommandSenderAsPlayer(sender), warp.dimension, (world, entity, yaw) -> {});
				player.setPositionAndRotation(0, 0, 0, warp.yaw, warp.pitch);
				player.setPositionAndUpdate(warp.x, warp.y, warp.z);
				Reference.teleportSafely(getCommandSenderAsPlayer(sender));
				Reference.sendMessage(sender, "You have been teleported.");
			} else Reference.sendMessage(sender, "The given warp does not exist.");
			else Reference.sendMessage(sender, WarpsHelper.getWarps(sender.getEntityWorld()).isEmpty() ? "There are currently no warps available for this world." : "Currently available warps for this world:\n" + WarpsHelper.getWarpsString(sender.getEntityWorld()));
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "warp", "Permission to use the warp command.", true);
		}

		protected String usage = "/warp [name] Teleports you to a warp.";

	}

}