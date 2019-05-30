package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class fly {

	public fly() {}

	public static class Commandfly extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "fly";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			boolean on = args.length > 0 && args[0].equals("on");
			boolean off = args.length > 0 && args[0].equals("off");
			if (args.length < 2) {
				EntityPlayer player = (EntityPlayer) sender;
				on = on || !player.capabilities.allowFlying && !off;
				player.capabilities.allowFlying = on;
				player.capabilities.isFlying = on;
				player.sendPlayerAbilities();
				Reference.sendMessage(player, "Flight mode has been turned " + (on ? TextFormatting.GREEN + "on" : TextFormatting.RED + "off") + Reference.dtf + ".");
			} else {
				EntityPlayer victim;
				try {
					victim = getPlayer(server, sender, args[1]);
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The given player does not exist.");
					return;
				}
				on = on || !victim.capabilities.allowFlying && !off;
				victim.capabilities.allowFlying = on;
				victim.capabilities.isFlying = on;
				victim.sendPlayerAbilities();
				Reference.sendMessage(victim, sender.getName() + " has made you " + (on ? "" + TextFormatting.GREEN : TextFormatting.RED + "un") + "able" + Reference.dtf + " to fly.");
				Reference.sendMessage(sender, "You made " + victim.getName() + " " + (on ? "" + TextFormatting.GREEN : TextFormatting.RED + "un") + "able" + Reference.dtf + " to fly.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "fly", "Permission to use the fly command.", true);
		}

		protected String usage = "/fly <on/off> [player]";

	}

}