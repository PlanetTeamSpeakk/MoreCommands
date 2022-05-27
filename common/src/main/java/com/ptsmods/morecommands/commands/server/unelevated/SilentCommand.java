package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class SilentCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literal("silent")
                .redirect(dispatcher.getRoot(), ctx -> ctx.getSource().withSilent()));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/silent";
    }
}
