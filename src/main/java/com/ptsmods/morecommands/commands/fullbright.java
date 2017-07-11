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
import net.minecraftforge.fml.common.FMLCommonHandler;

public class fullbright {

	public static Object instance;

	public fullbright() {
	}

	public static class Commandfullbright extends CommandBase {
		public boolean isUsernameIndex(int var1) {
			return false;
		}

		public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("fb");
			aliases.add("nightvision");
			aliases.add("nv");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "fullbright";
		}

		public String getUsage(ICommandSender var1) {
			return "/fullbright Gives you night vision.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] args) {
			EntityPlayer entity = (EntityPlayer) var1;
			if (entity instanceof EntityPlayerMP) {
				MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
				 if (entity instanceof EntityLivingBase) {
					 ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 20000000, 0, false, false));
					 var1.sendMessage(new TextComponentString("Now you can see anything! To remove the effect type /ce or /cleareffects."));
				 }
			}

		}

	}

}