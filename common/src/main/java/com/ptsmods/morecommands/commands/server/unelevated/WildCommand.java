package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.border.WorldBorder;

public class WildCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("wild")
                .executes(ctx -> {
                    WorldBorder border = ctx.getSource().getWorld().getWorldBorder();
                    double limit = Math.min(ctx.getSource().getWorld().getGameRules().getInt(MoreGameRules.get().wildLimitRule()), border.getSize()/2);
                    if (limit <= 0) sendError(ctx, "The wild is unreachable!");
                    else {
                        Entity entity = ctx.getSource().getEntityOrThrow();
                        double x = Math.random()*limit + border.getCenterX();
                        double z = Math.random()*limit + border.getCenterZ();
                        ctx.getSource().getEntityOrThrow().teleport(x, MoreCommands.getY(ctx.getSource().getWorld(), x, z), z);
                        sendMsg(ctx, "You have been teleported! Your new coords are " + SF + (int) x + DF + ", " + SF + entity.getBlockPos().getY() + DF + ", " + SF + (int) z + DF + ".");
                        return 1;
                    }
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/wild";
    }
}
