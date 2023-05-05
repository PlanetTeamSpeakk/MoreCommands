package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

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

    @Override
    public @Nullable Set<String> nodeNames() {
        return Collections.singleton("console");
    }
}
