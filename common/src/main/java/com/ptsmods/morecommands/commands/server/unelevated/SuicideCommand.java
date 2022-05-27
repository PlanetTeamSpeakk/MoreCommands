package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.server.command.ServerCommandSource;

public class SuicideCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("suicide")
                .executes(ctx -> {
                    ctx.getSource().getEntityOrThrow().damage(IMoreCommands.get().isServerOnly() ?
                            DamageSource.OUT_OF_WORLD : new EntityDamageSource("suicide", ctx.getSource().getEntity()), Float.MAX_VALUE);
                    ctx.getSource().getEntityOrThrow().kill();
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/suicide";
    }
}
