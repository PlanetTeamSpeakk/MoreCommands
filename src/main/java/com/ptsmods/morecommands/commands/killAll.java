package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class killAll {

	public static Object instance;

	public killAll() {
	}

	public static class CommandkillAll extends CommandBase {

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
			return "killall";
		}

		public String getUsage(ICommandSender var1) {
			return "/killall <entity> Kills all of the given entity in the world.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] args) {
			EntityPlayer entity = (EntityPlayer) var1;

			if (entity instanceof EntityPlayerMP) {
				MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
				if (minecraftserver != null) {
					if (args.length == 1) {
						if (args[0].equals("help")) {
							var1.sendMessage(new TextComponentString("For vanilla mobs you can just put in their name, e.g. creeper."));
							var1.sendMessage(new TextComponentString("But for modded mobs you must put the mod id, a :, and the mob name, e.g. thaumcraft:wisp."));
							var1.sendMessage(new TextComponentString("You can also just put in Player."));
							var1.sendMessage(new TextComponentString("Putting an exclamation mark in front of the mob will kill everything but that mob, e.g. !Player."));
						} else {
							minecraftserver.getCommandManager().executeCommand((EntityPlayerMP) entity, "kill @e[type=" + args[0] + "]");
						}
					} else {
						var1.sendMessage(new TextComponentString("You didn't give an entity, for help type /killall help."));
					}
				}
			}

		}

	}

}