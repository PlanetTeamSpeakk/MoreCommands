package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class smite {

	public smite() {
	}

	public static class Commandsmite extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			} else {
				return new ArrayList();
			}
		}

		public String getName() {
			return "smite";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {
				Reference.sendCommandUsage(sender, usage);
			} else {
				try {
					EntityPlayer victim = getPlayer(server, sender, args[0]);
					World world = victim.getEntityWorld();
					for (int x = 0; x < 3; x += 1) world.addWeatherEffect(new EntityLightningBolt(world, victim.getPosition().getX(), victim.getPosition().getY(), victim.getPosition().getZ(), false));
					Reference.sendMessage(sender, victim.getName() + " has been smitten.");
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The given player does not exist.");
				}
			}

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/smite <player> Strikes someone with lightning.";

	}

}