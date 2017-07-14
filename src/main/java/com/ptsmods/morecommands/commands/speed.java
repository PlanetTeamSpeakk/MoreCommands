package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class speed {

	public static Object instance;

	public speed() {
	}
	
	public static class Commandspeed extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
           ArrayList aliases = new ArrayList();
           aliases.add("sp");
           aliases.add("amfast");
           aliases.add("fastasfuckboii");
           return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return (java.util.List) (new java.util.ArrayList());
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "speed";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;

			if ((Integer.parseInt(args[0]) > 10) || (Integer.parseInt(args[0]) < 0)) {
				Reference.sendMessage(player, Reference.RED + "Usage:" + usage);
			} else {
				float speed = (float) Integer.parseInt(args[0]) / 10;
				player.capabilities.setFlySpeed(speed);
				player.capabilities.setPlayerWalkSpeed(speed);
				player.sendPlayerAbilities();
				Reference.sendMessage(sender, "Your move speed has been set to " + Float.toString(speed) + ".");
			}
		}
		
		protected String usage = "/speed <number> Makes you go faster, number should be a number between 0 and 10.";

	}

}
