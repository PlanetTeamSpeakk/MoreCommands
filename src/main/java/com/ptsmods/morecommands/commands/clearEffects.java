package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class clearEffects {

	public static Object instance;

	public clearEffects() {
	}

	public static class CommandclearEffects extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("ce");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new java.util.ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "cleareffects";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/cleareffects [player]";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			World world = player.getEntityWorld();
			if (!world.isRemote)
				if (args.length == 0) {
					if (player.getActivePotionEffects().isEmpty())
						Reference.sendMessage(sender, "You do not have any effects on.");
					else {
						player.clearActivePotions();
						Reference.sendMessage(sender, "All your effects have been cleared.");
					}
				} else
					try {
						EntityPlayer victim = CommandBase.getPlayer(server, sender, args[0]);
						if (victim.getActivePotionEffects().isEmpty())
							Reference.sendMessage(sender, victim.getName() + " does not have any potion effects.");
						else {
							victim.clearActivePotions();
							Reference.sendMessage(sender, victim.getName() + "'s potion effects have been cleared.");
						}
					} catch (PlayerNotFoundException e) {
						Reference.sendMessage(sender, "The given player does not exist.");
					}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "cleareffects", "Permission to use the cleareffects command.", true);
		}

	}

}
