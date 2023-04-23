package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;

public class WildCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("wild")
                .executes(ctx -> {
                    WorldBorder border = ctx.getSource().getLevel().getWorldBorder();
                    double limit = Math.min(ctx.getSource().getLevel().getGameRules().getInt(MoreGameRules.get().wildLimitRule()), border.getSize()/2);
                    if (limit <= 0) sendError(ctx, "The wild is unreachable!");
                    else {
                        Entity entity = ctx.getSource().getEntityOrException();
                        double x = Math.random()*limit + border.getCenterX();
                        double z = Math.random()*limit + border.getCenterZ();
                        ctx.getSource().getEntityOrException().teleportToWithTicket(x, MoreCommands.getY(ctx.getSource().getLevel(), x, z), z);
                        sendMsg(ctx, "You have been teleported! Your new coords are " + SF + (int) x + DF + ", " +
                                SF + Compat.get().blockPosition(entity).getY() + DF + ", " + SF + (int) z + DF + ".");
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
