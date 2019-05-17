package com.ptsmods.morecommands.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class noHunger {

	public noHunger() {}

	public static class CommandnoHunger extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				ArrayList options = new ArrayList();
				options.add("off");
				options.add("on");
				return options;
			} else if (args.length == 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "nohunger";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/nohunger <on/off> [player] Make you or someone else never go hungry again.";
		}

		@Override // wot 'n starvation
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) Reference.sendCommandUsage(sender, "/nohunger <on/off> [player] You, or someone else, will never be hungry anymore.");
			else if (args.length == 1) {
				EntityPlayer player = getCommandSenderAsPlayer(sender);
				if (args[0].equals("on")) {
					player.getFoodStats().setFoodLevel(20);
					setSaturation(player, Float.MAX_VALUE);
					Reference.sendMessage(player, "You will never be hungry anymore.");
				} else {
					setSaturation(player, 5F);
					Reference.sendMessage(sender, "You can get hungry again.");
				}
			} else try {
				EntityPlayer victim = getPlayer(server, sender, args[0]);
				if (args[0].equals("on")) {
					victim.getFoodStats().setFoodLevel(20);
					setSaturation(victim, Float.MAX_VALUE);
					Reference.sendMessage(victim, "You will never be hungry anymore, thanks to " + sender.getName() + ".");
					Reference.sendMessage(sender, victim.getName() + " will never be hungry anymore.");
				} else {
					setSaturation(victim, 5F);
					Reference.sendMessage(victim, "You can now get hungry again, thanks to " + sender.getName() + ".");
					Reference.sendMessage(sender, victim.getName() + " can now become hungry again.");
				}
			} catch (PlayerNotFoundException e) {
				Reference.sendMessage(sender, TextFormatting.RED + "The given player could not be found.");
				return;
			}
		}

		private void setSaturation(EntityPlayer player, float saturation) {
			try {
				Field f = FoodStats.class.getDeclaredField("foodSaturationLevel");
				f.setAccessible(true);
				f.setFloat(player.getFoodStats(), saturation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "nohunger", "Make you or someone else never go hungry again.", true);
		}

	}

}