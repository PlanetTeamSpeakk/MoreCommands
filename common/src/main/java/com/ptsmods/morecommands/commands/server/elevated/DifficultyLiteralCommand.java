package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.Difficulty;

public class DifficultyLiteralCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        for (Difficulty difficulty : Difficulty.values())
            dispatcher.register(literalReqOp(difficulty.getName())
                    .executes(ctx -> ctx.getSource().getServer().getCommandManager().getDispatcher().getRoot().getChild("difficulty").getChild(difficulty.getName()).getCommand().run(ctx)));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/difficulty-literal";
    }
}
