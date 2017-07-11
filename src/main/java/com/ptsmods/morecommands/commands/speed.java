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

public class speed {

	public static Object instance;

	public speed() {
	}
	
	public static class Commandspeed extends CommandBase {
		public boolean isUsernameIndex(int var1) {
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

		public String getUsage(ICommandSender var1) {
			return "/speed <number>";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] argString) {
			EntityPlayer entity = (EntityPlayer) var1;

               if ((argString.length == 0) || (Integer.parseInt(argString[0]) > 256) || (Integer.parseInt(argString[0]) < 1)) {
                    var1.sendMessage(new TextComponentString("Please fill in a number from 1 to 256."));
                    var1.sendMessage(new TextComponentString("Correct usage: /speed <number>"));
               } else {
     				if (entity instanceof EntityPlayerMP) {
     					MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
     					if (minecraftserver != null) {
                             argString[0] = new Integer(Integer.parseInt(argString[0]) - 1).toString();
							 if (entity instanceof EntityLivingBase) {
								 ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, 20000000, Integer.parseInt(argString[0]), false, false));
								 var1.sendMessage(new TextComponentString("Look at him go! To remove the speed effect or to lower it type /ce or /cleareffects."));
							 }
					     }
     				}
               }
		}

	}

}
