package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RealnameCommand extends Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("realname")
                .then(argument("query", StringArgumentType.word())
                        .executes(ctx -> {
                            String query = ctx.getArgument("query", String.class).toLowerCase();
                            List<ServerPlayer> players = new ArrayList<>();
                            for (ServerPlayer player : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                                Component nickname = MoreCommands.getNickname(player);
                                if (nickname == null) continue;

                                if (IMoreCommands.get().textToString(nickname, null, false).toLowerCase().contains(query) ||
                                        IMoreCommands.get().textToString(player.getName(), null, true).replace('\u00A7', '&').toLowerCase().contains(query))
                                    players.add(player);
                            }

                            if (players.size() == 0) sendError(ctx, "No players whose name matches the given query were found.");
                            else for (ServerPlayer player : players)
                                sendMsg(ctx, IMoreCommands.get().textToString(player.getDisplayName(), SS, true) + DF + " is " + IMoreCommands.get().textToString(player.getName(), SS, true));
                            return players.size();
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/realname";
    }
}
