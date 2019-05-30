package com.ptsmods.morecommands.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class speed {

	public speed() {}

	public static class Commandspeed extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("sp");
			aliases.add("amfast");
			aliases.add("fastasfuckboii");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new java.util.ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "speed";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			SpeedType type = args.length >= 2 && SpeedType.fromString(args[1]) != null ? SpeedType.fromString(args[1]) : player.capabilities.isFlying ? SpeedType.FLY : SpeedType.WALK;
			if (args.length == 0 || !Reference.isFloat(args[0]) || Float.parseFloat(args[0]) < 0) Reference.sendMessage(player, "Your " + TextFormatting.ITALIC + type + Reference.dtf + " speed is currently", TextFormatting.DARK_AQUA + "" + getSpeed(player, type) + Reference.dtf + ".");
			else if (Reference.isFloat(args[0])) Reference.sendMessage(sender, "Your " + TextFormatting.ITALIC + type.name().toLowerCase() + Reference.dtf + " speed has been set to " + TextFormatting.DARK_AQUA + setSpeed(player, type, Float.parseFloat(args[0])) + Reference.dtf + ".");
			else Reference.sendCommandUsage(sender, usage);
		}

		public static double getSpeed(EntityPlayer player, SpeedType type) {
			double speed = 0;
			switch (type) {
			case WALK:
			case FLY:
				try {
					Field f = type == SpeedType.FLY ? PlayerCapabilities.class.getDeclaredField("flySpeed") : PlayerCapabilities.class.getDeclaredField("walkSpeed");
					f.setAccessible(true);
					speed = f.getFloat(player.capabilities) * (type == SpeedType.FLY ? 20 : 10);
				} catch (Exception e) {
					e.printStackTrace();
					speed = -1;
				}
				break;
			}
			return speed;
		}

		public static double setSpeed(EntityPlayer player, SpeedType type, double speed) {
			switch (type) {
			case WALK:
			case FLY:
				try {
					Field f = type == SpeedType.FLY ? PlayerCapabilities.class.getDeclaredField("flySpeed") : PlayerCapabilities.class.getDeclaredField("walkSpeed");
					f.setAccessible(true);
					f.set(player.capabilities, (float) speed / 10 / (type == SpeedType.FLY ? 2 : 1));
					player.sendPlayerAbilities();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			return speed; // I like one-liners.
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "speed", "Permission to use the speed command.", true);
		}

		@Override
		public boolean singleplayerOnly() {
			return false;
		}

		protected String usage = "/speed <amount> [type] Makes you go faster, number should be a number between 0 and 100. Type should be either walk, fly or swim, default to walk if you're walking, fly if you're flying and swim if you're in a liquid. All values are 1 by default.";

		public static enum SpeedType {
			WALK, FLY;

			@Override
			public String toString() {
				return name().toLowerCase();
			}

			public static SpeedType fromString(String s) {
				switch (s.toLowerCase()) {
				case "walk":
					return WALK;
				case "fly":
					return FLY;
				default:
					return null;
				}
			}

		}

	}

}
