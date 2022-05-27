package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class CscoreCommand extends ClientCommand {

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("cscore")
                .then(cLiteral("teams")
                        .executes(ctx -> {
                            sendMsg(ctx.getSource().getTeamNames().isEmpty() ? "There are currently no teams." : "The current teams are " + joinNicely(ctx.getSource().getTeamNames()) + ".");
                            return ctx.getSource().getTeamNames().size();
                        }))
                .then(cLiteral("team")
                        .then(cArgument("team", StringArgumentType.word())
                                .executes(ctx -> sendTeamDetails(getTeam(ctx), false))
                                .then(cArgument("showMembers", BoolArgumentType.bool())
                                        .executes(ctx -> sendTeamDetails(getTeam(ctx), ctx.getArgument("showMembers", boolean.class))))))
                .then(cLiteral("objectives")
                        .executes(ctx -> {
                            List<String> strings = new ArrayList<>();
                            Scoreboard board = getScoreboard();
                            board.getObjectiveNames().forEach(obj -> strings.add(obj + ": " + board.getObjective(obj).getCriterion().getName()));
                            sendMsg(strings.isEmpty() ? "There are currently no objectives." : "The current objectives and their criteria are " + joinNicely(strings) + ".");
                            return strings.size();
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/client/c-score";
    }

    private int sendTeamDetails(Team team, boolean showMembers) {
        if (team == null) sendMsg(Formatting.RED + "A team by that name could not be found.");
        else sendMsg("The settings of the given team are as follows:" +
                "\n  Name: " + SF + team.getName() +
                "\n  Displayname: " + Formatting.WHITE + IMoreCommands.get().textToString(team.getDisplayName(), SS, true) +
                "\n  Prefix: " + IMoreCommands.get().textToString(team.getPrefix(), SS, true) +
                "\n  Suffix: " + IMoreCommands.get().textToString(team.getSuffix(), SS, true) +
                "\n  Friendly fire: " + (team.isFriendlyFireAllowed() ? Formatting.GREEN : Formatting.RED) + team.isFriendlyFireAllowed() +
                "\n  Collision: " + SF + team.getCollisionRule().name +
                "\n  Colour: " + team.getColor() + team.getColor().name() +
                "\n  Death message visibility: " + SF + team.getDeathMessageVisibilityRule().name +
                "\n  Nametag visibility: " + SF + team.getNameTagVisibilityRule().name +
                "\n  See friendly invisibles: " + (team.shouldShowFriendlyInvisibles() ? Formatting.GREEN : Formatting.RED) + team.shouldShowFriendlyInvisibles() +
                (showMembers ? "\n  Members: " + SF + joinNicely(team.getPlayerList()) + "." : ""));
        return team == null ? 0 : 1;
    }

    private Scoreboard getScoreboard() {
        return getWorld().getScoreboard();
    }

    private Team getTeam(CommandContext<ClientCommandSource> ctx) {
        return getScoreboard().getTeam(ctx.getArgument("team", String.class));
    }

}
