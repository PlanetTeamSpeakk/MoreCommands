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

public class clearEffects {

	public static Object instance;

	public clearEffects() {
	}
	
	public static class CommandclearEffects extends CommandBase {
		public boolean isUsernameIndex(int var1) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
           ArrayList aliases = new ArrayList();
           aliases.add("ce");
           return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return (java.util.List) (new java.util.ArrayList());
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "cleareffects";
		}

		public String getUsage(ICommandSender var1) {
			return "/speed <number>";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] argString) {
			EntityPlayer entity = (EntityPlayer) var1;

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
					 if (entity instanceof EntityLivingBase && !world.isRemote) {
						 if (entity.getActivePotionEffects().isEmpty()) {
							 var1.sendMessage(new TextComponentString("You do not have any effects on."));
						 } else {
							 entity.clearActivePotions();
							 var1.sendMessage(new TextComponentString("All your effects have been cleared."));
						 }
					 }
			     }
			}
		}

	}

}
