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

public class god {

	public static Object instance;

	public god() {
	}

	public static class Commandgod extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				ArrayList options = new ArrayList();
				options.add("on");
				options.add("off");
				return options;
			} else if (args.length == 2) {
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			} else {
				return new ArrayList();
			}
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "god";
		}

		public String getUsage(ICommandSender sender) {
			return this.usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
			if ((args.length == 0) || ((!args[0].equals("on")) && (!args[0].equals("off")))) {
				sender.sendMessage(new TextComponentString(Reference.RED + "Usage: " + this.usage));
			} else if (args.length == 1) { 
				if (args[0].equals("on")) {
					EntityPlayer player = (EntityPlayer) sender;
					player.setEntityInvulnerable(true);
					player.getFoodStats().setFoodLevel(20);
					player.getFoodStats().setFoodSaturationLevel(20000000F);
					sender.sendMessage(new TextComponentString("You're now invulnerable and will no longer be hungry."));
				} else {
					EntityPlayer player = (EntityPlayer) sender;
					player.setEntityInvulnerable(false);
					player.getFoodStats().setFoodSaturationLevel(5F);
					sender.sendMessage(new TextComponentString("You're no longer invulnerable and can be hungry."));
				}
			} else {
				if (args[0].equals("on")) {
					EntityPlayer victim = getPlayer(server, sender, args[1]);
					if (victim != null) {
						victim.setEntityInvulnerable(true);
						victim.getFoodStats().setFoodLevel(20);
						victim.getFoodStats().setFoodSaturationLevel(20000000F);
						if (victim == (EntityPlayer) sender) {
							EntityPlayer player = (EntityPlayer) sender;
							player.sendMessage(new TextComponentString("You're now invulnerable and will no longer be hungry."));
						} else {
							EntityPlayer player = (EntityPlayer) sender;
							victim.sendMessage(new TextComponentString(player.getName() + " made you invulnerable and you'll no longer be hungry."));
							player.sendMessage(new TextComponentString("You have made " + victim.getName() + " invulnerable."));
						}
					} else {
						sender.sendMessage(new TextComponentString("The given player does not exist."));
					}
				} else {
					EntityPlayer victim = getPlayer(server, sender, args[1]);
					if (victim != null) {
						victim.setEntityInvulnerable(false);
						victim.getFoodStats().setFoodSaturationLevel(5F);
						if (victim == (EntityPlayer) sender) {
							EntityPlayer player = (EntityPlayer) sender;
							player.sendMessage(new TextComponentString("You're now invulrnerable and will no longer be hungry."));
						} else {
							EntityPlayer player = (EntityPlayer) sender;
							victim.sendMessage(new TextComponentString(player.getName() + " made you invulnerable and you'll no longer be hungry."));
							player.sendMessage(new TextComponentString("You have made " + victim.getName() + " vulnerable."));
						}
					} else {
						sender.sendMessage(new TextComponentString("The given player does not exist."));
					}
				}
			}
		}
		
		protected String usage = "/god <on/off> [player] You'll never get damage again and you'll never be hungry anymore.";

	}

}