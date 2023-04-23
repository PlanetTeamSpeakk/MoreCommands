package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

// Smallest command class in the whole mod? :O
public class TimeLiteralCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String s : new String[] {"day", "noon", "night", "midnight"})
            dispatcher.register(literalReqOp(s)
                    .executes(ctx -> ctx.getSource().getServer().getCommands().getDispatcher().getRoot().getChild("time").getChild("set").getChild(s).getCommand().run(ctx)));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/time-literal";
    }
}
