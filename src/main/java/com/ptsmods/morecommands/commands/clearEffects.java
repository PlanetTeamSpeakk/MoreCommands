package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class clearEffects {

	public static Object instance;

	public clearEffects() {
	}
	
	public static class CommandclearEffects extends CommandBase {
		public boolean isUsernameIndex(int sender) {
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

		public String getUsage(ICommandSender sender) {
			return "/cleareffects [player]";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			World world = Reference.getWorld(server, player);
			 if (!world.isRemote) {
				 if (args.length == 0) {
					 if (player.getActivePotionEffects().isEmpty()) {
						 sender.sendMessage(new TextComponentString("You do not have any effects on."));
					 } else {
						 player.clearActivePotions();
						 sender.sendMessage(new TextComponentString("All your effects have been cleared."));
				     }
				 } else {
					 try {
						EntityPlayer victim = getPlayer(server, sender, args[0]);
					} catch (PlayerNotFoundException e) {
						sender.sendMessage(new TextComponentString("The given player does not exist."));
					}
				 }
			}
		}

	}

}
