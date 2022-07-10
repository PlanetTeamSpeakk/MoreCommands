package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class CscoreCommand extends ClientCommand {

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("cscore")
                .then(cLiteral("teams")
                        .executes(ctx -> {
                            sendMsg(ctx.getSource().getAllTeams().isEmpty() ? "There are currently no teams." : "The current teams are " + joinNicely(ctx.getSource().getAllTeams()) + ".");
                            return ctx.getSource().getAllTeams().size();
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
                            board.getObjectiveNames().forEach(obj -> strings.add(obj + ": " + board.getOrCreateObjective(obj).getCriteria().getName()));
                            sendMsg(strings.isEmpty() ? "There are currently no objectives." : "The current objectives and their criteria are " + joinNicely(strings) + ".");
                            return strings.size();
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/c-score";
    }

    private int sendTeamDetails(PlayerTeam team, boolean showMembers) {
        if (team == null) sendMsg(ChatFormatting.RED + "A team by that name could not be found.");
        else sendMsg("The settings of the given team are as follows:" +
                "\n  Name: " + SF + team.getName() +
                "\n  Displayname: " + ChatFormatting.WHITE + IMoreCommands.get().textToString(team.getDisplayName(), SS, true) +
                "\n  Prefix: " + IMoreCommands.get().textToString(team.getPlayerPrefix(), SS, true) +
                "\n  Suffix: " + IMoreCommands.get().textToString(team.getPlayerSuffix(), SS, true) +
                "\n  Friendly fire: " + (team.isAllowFriendlyFire() ? ChatFormatting.GREEN : ChatFormatting.RED) + team.isAllowFriendlyFire() +
                "\n  Collision: " + SF + team.getCollisionRule().name +
                "\n  Colour: " + team.getColor() + team.getColor().name() +
                "\n  Death message visibility: " + SF + team.getDeathMessageVisibility().name +
                "\n  Nametag visibility: " + SF + team.getNameTagVisibility().name +
                "\n  See friendly invisibles: " + (team.canSeeFriendlyInvisibles() ? ChatFormatting.GREEN : ChatFormatting.RED) + team.canSeeFriendlyInvisibles() +
                (showMembers ? "\n  Members: " + SF + joinNicely(team.getPlayers()) + "." : ""));
        return team == null ? 0 : 1;
    }

    private Scoreboard getScoreboard() {
        return getWorld().getScoreboard();
    }

    private PlayerTeam getTeam(CommandContext<ClientSuggestionProvider> ctx) {
        return getScoreboard().getPlayerTeam(ctx.getArgument("team", String.class));
    }

}
