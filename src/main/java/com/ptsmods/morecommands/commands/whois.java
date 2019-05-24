package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.FPProvider;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.ReachProvider;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class whois {

	public whois() {}

	public static class Commandwhois extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
		}

		@Override
		public String getName() {
			return "whois";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length != 0) {
				EntityPlayerMP victim = (EntityPlayerMP) Reference.getPlayer(server, args[0]);
				if (victim == null) Reference.sendMessage(sender, "The given player could not be found.");
				String ip = "unknown";
				try {
					ip = victim.getPlayerIP() == null ? "unknown" : victim.getPlayerIP().equals("local") ? Reference.getIPAddress() : victim.getPlayerIP();
				} catch (Throwable e) {}
				String health = Float.toString(victim.getHealth());
				String stamina = Integer.toString(victim.getFoodStats().getFoodLevel());
				String exp = Integer.toString(victim.experienceTotal);
				String expLevel = Integer.toString(victim.experienceLevel);
				String location = TextFormatting.GOLD + "\n    X: " + TextFormatting.YELLOW + victim.getPosition().getX() + TextFormatting.GOLD + "\n    Y: " + TextFormatting.YELLOW + victim.getPosition().getY() + TextFormatting.GOLD + "\n    Z: " + TextFormatting.YELLOW + victim.getPosition().getZ();
				String country = "";
				try {
					country = Reference.getHTML("http://ip-api.com/json/" + ip).split("\",\"")[2].split(":")[1];
					country = ip.equals("unknown") || ip.equals("127.0.0.1") ? "unknown" : country.substring(1);
				} catch (IOException e) {
					country = "Error getting country.";
				}
				String gamemode = victim.interactionManager.getGameType().getName();
				String godEnabled = Boolean.toString(victim.getIsInvulnerable());
				String isOp = Boolean.toString(Reference.isOp(victim));
				String isFlying = Boolean.toString(victim.capabilities.isFlying);
				String canFly = Boolean.toString(victim.capabilities.allowFlying);
				String speed = "\n    Walk: " + TextFormatting.YELLOW + victim.capabilities.getWalkSpeed() * 10 + TextFormatting.GOLD + "\n    Fly: " + TextFormatting.YELLOW + victim.capabilities.getFlySpeed() * 20 + TextFormatting.GOLD + "\n    Swim: " + TextFormatting.YELLOW + victim.getEntityAttribute(EntityLivingBase.SWIM_SPEED).getAttributeValue();
				String reach = "" + victim.getCapability(ReachProvider.reachCap, null).get();
				String isFake = "" + victim.getCapability(FPProvider.fpCap, null).isFake;
				Reference.sendMessage(sender, // @formatter:off
						"Whois lookup for " + TextFormatting.YELLOW + victim.getName() +
						"\nUUID: " + TextFormatting.YELLOW + victim.getUniqueID().toString() +
						"\nIP address: " + TextFormatting.YELLOW + ip +
						"\nCountry: " + TextFormatting.YELLOW + country +
						"\nHealth: " + TextFormatting.YELLOW + health +
						"\nStamina: " + TextFormatting.YELLOW + stamina +
						"\nExp: " + TextFormatting.YELLOW + exp +
						"\nExp level: " + TextFormatting.YELLOW + expLevel +
						"\nLocation: " + TextFormatting.YELLOW + location +
						"\nGamemode: " + TextFormatting.YELLOW + gamemode +
						"\nSpeed: " + TextFormatting.YELLOW + speed +
						"\nReach: " + TextFormatting.YELLOW + reach +
						"\nIs fake: " + Reference.getColorFromBoolean(isFake) + isFake +
						"\nGod enabled: " + Reference.getColorFromBoolean(godEnabled) + godEnabled +
						"\nIs op: " + Reference.getColorFromBoolean(isOp) + isOp +
						"\nIs flying: " + Reference.getColorFromBoolean(isFlying) + isFlying +
						"\nCan fly: " + Reference.getColorFromBoolean(canFly) + canFly);//@formatter:on
			} else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "whois", "Permission to use the whois command.", true);
		}

		protected String usage = "/whois <player> Get all of a player's information, including their ip, you hackerman.";

	}

}