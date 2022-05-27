package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class ConsoleCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("console")
                .redirect(dispatcher.getRoot(), ctx -> ctx.getSource().getServer().getCommandSource()));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/console";
    }
}
