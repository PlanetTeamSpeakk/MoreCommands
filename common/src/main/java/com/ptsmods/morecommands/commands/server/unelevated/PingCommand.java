package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class PingCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("ping").executes(ctx -> sendMsg(ctx, "Pong! " + ctx.getSource().getPlayerOrThrow().pingMilliseconds + " ms latency")));
    }
}
