package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class heal {

	public heal() {
	}

	public static class Commandheal extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("feed");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1)
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else
				return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "heal";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/heal [player] Heals and feeds you or someone else, be nice for once...";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			Blocks.GRASS.setBlockUnbreakable();
			EntityPlayer player = (EntityPlayer) sender;
			if (args.length >= 2)
				player = getPlayer(server, sender, args[1]);
			player.setHealth(20F);
			player.getFoodStats().setFoodLevel(20);
			if (server.isSinglePlayer()) player.getFoodStats().setFoodSaturationLevel(5F);
			if (player == (EntityPlayer) sender) Reference.sendMessage(player, "You're now healed and fed.");
			else {
				Reference.sendMessage(player, sender.getName() + " has healed and fed you.");
				Reference.sendMessage(sender, player.getName() + " has been healed and fed.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "heal", "Permission to use the heal command.", true);
		}

	}

}