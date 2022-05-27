package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Collection;

public class EcloneCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("eclone")
                .executes(ctx -> {
                    HitResult result = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160D, false, true);
                    if (result.getType() == HitResult.Type.ENTITY) {
                        Entity hit = ((EntityHitResult) result).getEntity();
                        if (!(hit instanceof PlayerEntity)) {
                            Entity e = MoreCommands.cloneEntity(hit, true);
                            if (e != null) {
                                sendMsg(ctx, "Successfully cloned entity of type " + IMoreCommands.get().textToString(e.getType().getName(), SS, true) + DF + ".");
                                return 1;
                            } else sendMsg(ctx, Formatting.DARK_RED + "The entity could not be cloned, huh.");
                        } else sendError(ctx, "Players cannot be cloned.");
                    } else sendError(ctx, "It appears you're not looking at an entity.");
                    return 0;
                })
                .then(argument("entities", EntityArgumentType.entities())
                        .executes(ctx -> {
                            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "entities");
                            int success = 0;
                            int fail = 0;
                            for (Entity e : entities) {
                                if (MoreCommands.cloneEntity(e, true) != null) success++;
                                else fail++;
                            }

                            sendMsg(ctx, SF + "" + success + " entit" + (success == 1 ? "y " + DF + "was " : "ies " + DF + "were ") + Formatting.GREEN + "successfully" + DF + " cloned" + (fail == 0 ? "." : " while " + SF + "" + fail + " entit" + (fail == 1 ? "y " + DF + "was " : "ies " + DF + "were ") + Formatting.RED + "not" + DF + "."));
                            return success;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/eclone";
    }
}
