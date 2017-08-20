package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

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

	public static class Commandgod extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if ((args.length == 0) || ((!args[0].equals("on")) && (!args[0].equals("off")))) {
				Reference.sendCommandUsage(sender, usage);
			} else {
				EntityPlayer player = (EntityPlayer) sender;
				if (args.length == 2) {
					try {
						player = getPlayer(server, sender, args[1]);
					} catch (PlayerNotFoundException e) {
						Reference.sendMessage(sender, "The given player could not be found.");
					}
				}
				if (args[0].equals("on")) {
					player.setEntityInvulnerable(true);
					Reference.sendMessage(sender, "You're now invulnerable.");
				} else {
					player.setEntityInvulnerable(false);
					Reference.sendMessage(sender, "You're no longer invulnerable.");
				}
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/god <on/off> [player] You'll never get damage again and you'll never be hungry anymore.";

	}

}