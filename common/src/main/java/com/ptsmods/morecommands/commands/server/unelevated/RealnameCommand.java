package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class RealnameCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("realname")
                .then(argument("query", StringArgumentType.word())
                        .executes(ctx -> {
                            String query = ctx.getArgument("query", String.class).toLowerCase();
                            List<ServerPlayerEntity> players = new ArrayList<>();
                            for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList())
                                if (IMoreCommands.get().textToString(MoreCommands.getNickname(player), null, false).toLowerCase().contains(query) ||
                                        IMoreCommands.get().textToString(player.getName(), null, true).toLowerCase().contains(query))
                                    players.add(player);

                            if (players.size() == 0) sendError(ctx, "No players whose name matches the given query were found.");
                            else for (ServerPlayerEntity player : players)
                                sendMsg(ctx, IMoreCommands.get().textToString(player.getDisplayName(), SS, true) + DF + " is " + IMoreCommands.get().textToString(player.getName(), SS, true));
                            return players.size();
                        })));
    }
}
