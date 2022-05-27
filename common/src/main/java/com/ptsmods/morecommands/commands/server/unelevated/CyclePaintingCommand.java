package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.arguments.PaintingVariantArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class CyclePaintingCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("cyclepainting")
                .executes(ctx -> execute(ctx, null))
                .then(argument("motive", PaintingVariantArgumentType.paintingVariant())
                        .executes(ctx -> execute(ctx, PaintingVariantArgumentType.getPaintingVariant(ctx, "motive")))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, PaintingVariant motive) throws CommandSyntaxException {
        HitResult result = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160F, false, true);
        if (result.getType() == HitResult.Type.ENTITY && ((EntityHitResult) result).getEntity() instanceof PaintingEntity) {
            PaintingEntity painting = (PaintingEntity) ((EntityHitResult) result).getEntity();
            PaintingVariant oldArt = (PaintingVariant) Compat.get().getPaintingVariant(painting);
            Compat.get().setPaintingVariant(painting, motive == null ? Registry.PAINTING_VARIANT.get((Registry.PAINTING_VARIANT.getRawId(oldArt)+1) % Registry.PAINTING_VARIANT.size()) : motive);
            BlockPos pos = painting.getBlockPos();
            Entity painting0 = MoreCommands.cloneEntity(painting, false);
            painting.kill();
            painting0.setPos(pos.getX(), pos.getY(), pos.getZ());
            ctx.getSource().getWorld().spawnEntity(painting0);
            return 1;
        } else sendError(ctx, "It appears as if you're not looking at a painting.");
        return 0;
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/cycle-painting";
    }
}
