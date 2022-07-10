package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

public class PingCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("ping")
                .executes(ctx -> sendMsg(ctx, "Pong! " + ctx.getSource().getPlayerOrException().latency + " ms latency")));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/ping";
    }
}
