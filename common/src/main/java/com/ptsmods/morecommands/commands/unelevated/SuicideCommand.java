package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

public class SuicideCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("suicide")
                .executes(ctx -> {
                    ctx.getSource().getEntityOrException().hurt(Compat.get().getSuicideDamageSource(ctx.getSource().getEntityOrException()), Float.MAX_VALUE);
                    ctx.getSource().getEntityOrException().kill();
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/suicide";
    }
}
