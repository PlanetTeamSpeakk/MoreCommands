package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class heal {

	public static Object instance;

	public heal() {
	}

	public static class Commandheal extends CommandBase {
		public boolean isUsernameIndex(int var1) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			return new ArrayList();
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "heal";
		}

		public String getUsage(ICommandSender var1) {
			return "/heal Heals and feeds you.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] cmd) {
			int i = var1.getPosition().getX();
			int j = var1.getPosition().getY();
			int k = var1.getPosition().getZ();
			EntityPlayer entity = (EntityPlayer) var1;

			int x = i;
			int y = j;
			int z = k;

			World world = null;
			WorldServer[] list = server.worlds;
			for (WorldServer ins : list) {
				if (ins.provider.getDimension() == entity.world.provider.getDimension())
					world = ins;
			}
			if (world == null)
				world = list[0];

			if (entity instanceof EntityPlayerMP) {
				MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
				if (minecraftserver != null) {
					if (entity instanceof EntityLivingBase) {
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SATURATION, 10, 255, false, false));
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 10, 255, false, false));
						var1.sendMessage(new TextComponentString("You're now healed and fed."));
					}
				}

			}
		}
		public int compareTo(ICommand c) {
			return getName().compareTo(c.getName());
		}

	}

}