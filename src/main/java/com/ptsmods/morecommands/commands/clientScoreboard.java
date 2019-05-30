package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class clientScoreboard {

	public clientScoreboard() {}

	public static class CommandclientScoreboard extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			return Lists.newArrayList("clientscoreboard", "clientscores", "cscoreboard", "cscores", "clientscore", "clientscores");
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "cscore";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				Scoreboard board = sender.getEntityWorld().getScoreboard();
				switch (args[0]) {
				case "teams":
					Reference.sendMessage(sender, "The following teams are known:\n" + joinNiceStringFromCollection(board.getTeamNames()));
					break;
				case "team":
					if (args.length == 1) Reference.sendCommandUsage(sender, "/cscore team <player/team> [show members] Get the team the given player is in or get the settings set of the given team. If a team and not a player is given, it will show members by default unless set otherwise.");
					else if (board.getTeam(args[1]) != null) {
						ScorePlayerTeam team = board.getTeam(args[1]); //@formatter:off
						Reference.sendMessage(sender, "The settings of the given team are as follows:" +
						"\n  Name: " + TextFormatting.YELLOW + team.getName() +
						"\n  Displayname: " + TextFormatting.WHITE + team.getDisplayName() +
						"\n  Prefix: " + team.getPrefix() +
						"\n  Suffix: " + team.getSuffix() +
						"\n  Friendly fire: " + Reference.getColorFromBoolean(team.getAllowFriendlyFire()) + team.getAllowFriendlyFire() +
						"\n  Collision: " + TextFormatting.YELLOW + team.getCollisionRule().name +
						"\n  Colour: " + team.getColor() + team.getColor().name() +
						"\n  Death message visibility: " + TextFormatting.YELLOW + team.getDeathMessageVisibility().internalName +
						"\n  Nametag visibility: " + TextFormatting.YELLOW + team.getNameTagVisibility().internalName +
						"\n  See friendly invisibles: " + Reference.getColorFromBoolean(team.getSeeFriendlyInvisiblesEnabled()) + team.getSeeFriendlyInvisiblesEnabled() +
						(args.length >= 3 && Reference.isBoolean(args[2]) && Boolean.parseBoolean(args[2]) || args.length < 3 || !Reference.isBoolean(args[2]) ? "\n  Members: " + TextFormatting.YELLOW + joinNiceStringFromCollection(team.getMembershipCollection()) : ""));
						//@formatter:on
					} else {
						ScorePlayerTeam team = board.getPlayersTeam(args[1]);
						if (team == null) Reference.sendMessage(sender, TextFormatting.RED + "The given player could either not be found or is not in a team and no team with the given name could be found either.");
						else Reference.sendMessage(sender, "The player '" + args[1] + "' is in the team '" + team.getDisplayName() + Reference.dtf + "'.");
					}
					break;
				case "objectives":
					Reference.sendMessage(sender, "The following objectives were found:\n" + joinNiceStringFromCollection(board.getObjectiveNames()));
					break;
				default:
					Reference.sendCommandUsage(sender, usage);
					break;
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "PERMISSION", "DESCRIPTION", false);
		}

		private String usage = "/cscore <teams|team|objectives> Read-only scoreboard commands on the client.";

	}

}