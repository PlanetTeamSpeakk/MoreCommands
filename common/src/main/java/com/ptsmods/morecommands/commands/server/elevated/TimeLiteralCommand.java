package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

// Smallest command class in the whole mod? :O
public class TimeLiteralCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        for (String s : new String[] {"day", "noon", "night", "midnight"})
            dispatcher.register(literalReqOp(s)
                    .executes(ctx -> ctx.getSource().getServer().getCommandManager().getDispatcher().getRoot().getChild("time").getChild("set").getChild(s).getCommand().run(ctx)));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/time-literal";
    }
}
