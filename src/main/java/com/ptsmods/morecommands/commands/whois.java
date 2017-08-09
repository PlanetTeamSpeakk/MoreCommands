package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class whois {

	public whois() {
	}

	public static class Commandwhois extends CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			} else return new ArrayList();
		}

		public String getName() {
			return "whois";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length != 0) {
				EntityPlayerMP victim;
				try {
					victim = ((EntityPlayerMP) getPlayer(server, sender, args[0]));
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(sender, "The given player could not be found.");
					return;
				}
				String ip = victim.getPlayerIP();
				String health = Float.toString(victim.getHealth());
				String stamina = Float.toString(victim.getFoodStats().getFoodLevel());
				String exp = Float.toString(victim.experienceTotal);
				String expLevel = Float.toString(victim.experienceLevel);
				String location = "X: " + victim.getPosition().getX() + ", Y: " + victim.getPosition().getY() + ", Z: " + victim.getPosition().getZ() + ", Biome: " +
							victim.getEntityWorld().getBiome(victim.getPosition()).getBiomeName();
				String country = "";
				try {
					country = Reference.getHTML("http://ip-api.com/json/" + ip).split("\",\"")[2].split(":")[1];
					country = (ip.equals("local") ? "Unknown" : country.substring(1, country.length()-1));
				} catch (IOException e) {
					country = "Error getting country.";
				}
				String gamemode = victim.interactionManager.getGameType().getName();
				String godEnabled = Boolean.toString(victim.getIsInvulnerable());
				String isOp = Boolean.toString(victim.canUseCommand(victim.getServer().getOpPermissionLevel(), "barrier"));
				String isFlying = Boolean.toString(victim.capabilities.isFlying);
				Reference.sendMessage(sender, 
						"Victim: " + victim.getName() +
						"\nIP address: " + ip +
						"\nCountry: " + country +
						"\nHealth: " + health +
						"\nStamina: " + stamina +
						"\nExp: " + exp +
						"\nExp level: " + expLevel +
						"\nLocation: " + location +
						"\nGamemode: " + gamemode +
						"\nGod enabled: " + Reference.getColorFromBoolean(godEnabled) + godEnabled +
						"\nIs op: " + Reference.getColorFromBoolean(isOp) + isOp +
						"\nIs flying: " + Reference.getColorFromBoolean(isFlying) + isFlying);
			} else Reference.sendCommandUsage(sender, usage);

		}
		
		protected String usage = "/whois <player> Get all of a player's information, including their ip, you hackerman.";

	}

}