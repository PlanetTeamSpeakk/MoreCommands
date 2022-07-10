package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.Difficulty;

public class DifficultyLiteralCommand extends Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (Difficulty difficulty : Difficulty.values())
            dispatcher.register(literalReqOp(difficulty.getKey())
                    .executes(ctx -> ctx.getSource().getServer().getCommands().getDispatcher().getRoot().getChild("difficulty").getChild(difficulty.getKey()).getCommand().run(ctx)));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/difficulty-literal";
    }
}
