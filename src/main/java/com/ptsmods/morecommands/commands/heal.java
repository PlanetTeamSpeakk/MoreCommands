package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class heal {

	public static Object instance;

	public heal() {
	}

	public static class Commandheal extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			return new ArrayList();
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			} else {
				return new ArrayList();
			}
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "heal";
		}

		public String getUsage(ICommandSender sender) {
			return "/heal [player] Heals and feeds you or someone else, be nice for once...";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = (EntityPlayer) sender;

			if (player instanceof EntityPlayerMP) {
				MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
				if (minecraftserver != null) {
					if (args.length == 0) {
						player.setHealth(20F);
						player.getFoodStats().setFoodLevel(20);
						player.getFoodStats().setFoodSaturationLevel(20F);
						sender.sendMessage(new TextComponentString("You're now healed and fed."));
					} else {
						try {
							EntityPlayer victim = getPlayer(server, sender, args[0]);
							victim.setHealth(20F);
							victim.getFoodStats().setFoodLevel(20);
							victim.getFoodStats().setFoodSaturationLevel(20F);
							if (victim != player) {
								victim.sendMessage(new TextComponentString(sender.getName() + " healed and fed you."));
								sender.sendMessage(new TextComponentString(victim.getName() + " has been healed and fed."));
							} else {
								sender.sendMessage(new TextComponentString("You're now healed and fed."));
							}
						} catch (PlayerNotFoundException e) {
							sender.sendMessage(new TextComponentString("The given player does not exist."));
							return;
						}
					}
				}

			}
		}
		public int compareTo(ICommand c) {
			return getName().compareTo(c.getName());
		}

	}

}