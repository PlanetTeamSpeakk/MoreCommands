package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class EcloneCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("eclone")
                .executes(ctx -> {
                    HitResult result = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160D, false, true);
                    if (result.getType() == HitResult.Type.ENTITY) {
                        Entity hit = ((EntityHitResult) result).getEntity();
                        if (!(hit instanceof Player)) {
                            Entity e = MoreCommands.cloneEntity(hit, true);
                            if (e != null) {
                                sendMsg(ctx, "Successfully cloned entity of type " + IMoreCommands.get().textToString(e.getType().getDescription(), SS, true) + DF + ".");
                                return 1;
                            } else sendMsg(ctx, ChatFormatting.DARK_RED + "The entity could not be cloned, huh.");
                        } else sendError(ctx, "Players cannot be cloned.");
                    } else sendError(ctx, "It appears you're not looking at an entity.");
                    return 0;
                })
                .then(argument("entities", EntityArgument.entities())
                        .executes(ctx -> {
                            Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "entities");
                            int success = 0;
                            int fail = 0;
                            for (Entity e : entities) {
                                if (MoreCommands.cloneEntity(e, true) != null) success++;
                                else fail++;
                            }

                            sendMsg(ctx, SF + "" + success + " entit" + (success == 1 ? "y " + DF + "was " : "ies " + DF + "were ") + ChatFormatting.GREEN + "successfully" + DF + " cloned" + (fail == 0 ? "." : " while " + SF + "" + fail + " entit" + (fail == 1 ? "y " + DF + "was " : "ies " + DF + "were ") + ChatFormatting.RED + "not" + DF + "."));
                            return success;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/eclone";
    }
}
