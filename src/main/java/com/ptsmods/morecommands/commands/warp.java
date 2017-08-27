package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class warp {

	public warp() {
	}

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
			return new ArrayList();
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
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0)
				try {
					if (Reference.doesWarpExist(args[0])) {
						Map<String, Double> data = Reference.warps.get(args[0]);
						EntityPlayer player = (EntityPlayer) sender;
						player.setPositionAndRotation(0, 0, 0, Reference.doubleToFloat(data.get("yaw")), Reference.doubleToFloat(data.get("pitch")));
						player.setPositionAndUpdate(data.get("x"), data.get("y"), data.get("z"));
						Reference.sendMessage(sender, "You have been teleported.");
					} else Reference.sendMessage(sender, "The given warp does not exist.");
				} catch (IOException e) {
					Reference.sendMessage(sender, TextFormatting.RED + "An unknown error occured while attempting to perform this command");
				}
			else Reference.sendMessage(sender, Reference.getWarps().length == 0 ? "There are currently no warps available." : "Currently available warps:\n" + Reference.getWarpsString());
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