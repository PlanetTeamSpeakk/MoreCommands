package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class HealCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("feed", dispatcher.register(literalReqOp("heal")
                .executes(ctx -> execute(ctx.getSource().getPlayerOrException()))
                .then(argument("players", EntityArgument.players())
                        .executes(ctx -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "players");
                            players.forEach(this::execute);
                            sendMsg(ctx, "Healed " + SF + players.size() + DF + " players.");
                            return players.size();
                        })))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/heal";
    }

    private int execute(ServerPlayer player) {
        player.setHealth(player.getMaxHealth());
        player.getFoodData().eat(20, 20);
        sendMsg(player, "You have been healed and fed.");
        return 1;
    }
}
