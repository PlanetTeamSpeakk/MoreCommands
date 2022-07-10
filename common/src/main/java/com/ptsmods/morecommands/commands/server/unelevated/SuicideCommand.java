package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;

public class SuicideCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("suicide")
                .executes(ctx -> {
                    ctx.getSource().getEntityOrException().hurt(IMoreCommands.get().isServerOnly() ?
                            DamageSource.OUT_OF_WORLD : new EntityDamageSource("suicide", ctx.getSource().getEntity()), Float.MAX_VALUE);
                    ctx.getSource().getEntityOrException().kill();
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/suicide";
    }
}
