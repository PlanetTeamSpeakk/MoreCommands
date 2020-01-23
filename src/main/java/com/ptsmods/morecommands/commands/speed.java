package com.ptsmods.morecommands.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
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
			aliases.add("fast");
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
			SpeedType type = args.length >= 2 && SpeedType.fromString(args[1]) != null ? SpeedType.fromString(args[1]) : player.isInWater() || player.isInLava() ? SpeedType.SWIM : player.capabilities.isFlying ? SpeedType.FLY : SpeedType.WALK;
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
					Field f = type == SpeedType.FLY ? Reference.getFieldMapped(PlayerCapabilities.class, "flySpeed", "field_149116_e", "field_149497_e", "field_75096_f") : Reference.getFieldMapped(PlayerCapabilities.class, "walkSpeed", "field_149114_f", "field_149495_f", "field_75097_g");
					f.setAccessible(true);
					speed = f.getFloat(player.capabilities) * (type == SpeedType.FLY ? 20 : 10);
				} catch (Exception e) {
					e.printStackTrace();
					speed = -1;
				}
				break;
			case SWIM:
				speed = player.getEntityAttribute(EntityLivingBase.SWIM_SPEED).getAttributeValue();
				break;
			}
			return speed;
		}

		public static double setSpeed(EntityPlayer player, SpeedType type, double speed) {
			switch (type) {
			case WALK:
			case FLY:
				try {
					Field f = type == SpeedType.FLY ? Reference.getFieldMapped(PlayerCapabilities.class, "flySpeed", "field_149116_e", "field_149497_e", "field_75096_f") : Reference.getFieldMapped(PlayerCapabilities.class, "walkSpeed", "field_149114_f", "field_149495_f", "field_75097_g");
					f.setAccessible(true);
					f.set(player.capabilities, (float) speed / 10 / (type == SpeedType.FLY ? 2 : 1));
					player.sendPlayerAbilities();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case SWIM:
				player.getEntityAttribute(EntityLivingBase.SWIM_SPEED).setBaseValue(speed);
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
			return new Permission(Reference.MOD_ID, "speed", "Change your walk, fly and even swim speed.", true);
		}

		@Override
		public boolean singleplayerOnly() {
			return false;
		}

		protected String usage = "/speed <amount> [type] Makes you go faster, number should be a number between 0 and 100. Type should be either walk, fly or swim, default to walk if you're walking, fly if you're flying and swim if you're in a liquid. All values are 1 by default.";

		public static enum SpeedType {
			WALK, FLY, SWIM;

			@Override
			public String toString() {
				return name().toLowerCase();
			}

			public static SpeedType fromString(String s) {
				for (SpeedType type : values())
					if (type.toString().equalsIgnoreCase(s)) return type;
				return null;
			}

		}

	}

}
