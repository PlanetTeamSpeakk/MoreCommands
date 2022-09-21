package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.arguments.PaintingVariantArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class CyclePaintingCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("cyclepainting")
                .executes(ctx -> execute(ctx, null))
                .then(argument("motive", PaintingVariantArgumentType.paintingVariant())
                        .executes(ctx -> execute(ctx, PaintingVariantArgumentType.getPaintingVariant(ctx, "motive")))));
    }

    private int execute(CommandContext<CommandSourceStack> ctx, PaintingVariant motive) throws CommandSyntaxException {
        HitResult result = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160F, false, true);
        if (result.getType() == HitResult.Type.ENTITY && ((EntityHitResult) result).getEntity() instanceof Painting) {
            Painting painting = (Painting) ((EntityHitResult) result).getEntity();
            PaintingVariant oldArt = (PaintingVariant) Compat.get().getPaintingVariant(painting);
            Compat.get().setPaintingVariant(painting, motive == null ? Registry.PAINTING_VARIANT.byId((Registry.PAINTING_VARIANT.getId(oldArt)+1) % Registry.PAINTING_VARIANT.size()) : motive);
            BlockPos pos = Compat.get().blockPosition(painting);
            Entity painting0 = MoreCommands.cloneEntity(painting, false);
            painting.kill();
            painting0.setPosRaw(pos.getX(), pos.getY(), pos.getZ());
            ctx.getSource().getLevel().addFreshEntity(painting0);
            return 1;
        } else sendError(ctx, "It appears as if you're not looking at a painting.");
        return 0;
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/cycle-painting";
    }
}
