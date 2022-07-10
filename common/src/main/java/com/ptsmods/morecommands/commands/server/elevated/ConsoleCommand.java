package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

public class ConsoleCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("console")
                .redirect(dispatcher.getRoot(), ctx -> ctx.getSource().getServer().createCommandSourceStack()));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/console";
    }
}
