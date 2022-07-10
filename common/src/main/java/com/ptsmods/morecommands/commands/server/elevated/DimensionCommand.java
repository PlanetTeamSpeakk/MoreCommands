package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class DimensionCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("dimension")
                .then(argument("dimension", DimensionArgument.dimension())
                        .executes(ctx -> execute(ctx, DimensionArgument.getDimension(ctx, "dimension"), ctx.getSource().getEntityOrException()))
                        .then(argument("player", EntityArgument.player())
                                .executes(ctx -> execute(ctx, DimensionArgument.getDimension(ctx, "dimension"), EntityArgument.getPlayer(ctx, "player"))))));
    }

    private int execute(CommandContext<CommandSourceStack> ctx, ServerLevel world, Entity entity) {
        if (entity.getCommandSenderWorld() != world) {
            MoreCommands.teleport(entity, world, entity.getX(), entity.getY(), entity.getZ(), ((MixinEntityAccessor) entity).getYRot_(), ((MixinEntityAccessor) entity).getXRot_());
            return 1;
        }
        sendError(ctx, "The targeted entity is already in that world.");
        return 0;
    }

    @Override
    public String getDocsPath() {
        return "/elevated/dimension";
    }
}
