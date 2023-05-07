package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class CyclePaintingCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("cyclepainting")
                .executes(ctx -> execute(ctx, null))
                .then(this.<CommandSourceStack>newResourceArgument("variant", Version.getCurrent().isNewerThanOrEqual(Version.V1_19) ?
                        "painting_variant" : "motive")
                        .executes(ctx -> execute(ctx, ctx.getArgument("variant", ResourceLocation.class)))));
    }

    private int execute(CommandContext<CommandSourceStack> ctx, ResourceLocation variant) throws CommandSyntaxException {
        if (variant != null && !Compat.get().getBuiltInRegistry(Version.getCurrent().isNewerThanOrEqual(Version.V1_19) ?
                "painting_variant" : "motive").containsKey(variant))
            return sendError(ctx, "That painting variant does not exist.");

        HitResult result = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160F, false, true);
        if (result.getType() != HitResult.Type.ENTITY || !(((EntityHitResult) result).getEntity() instanceof Painting painting)) {
            sendError(ctx, "You don't seem to be looking at a painting.");
            return 0;
        }

        ResourceLocation oldArt = Compat.get().getPaintingVariant(painting);
        Compat.get().setPaintingVariant(painting, variant == null ? Compat.get().nextPaintingVariant(oldArt) : variant);

        if (Version.getCurrent().isOlderThan(Version.V1_19)) return 1; // Below code only necessary on old versions
        BlockPos pos = Compat.get().blockPosition(painting);
        Entity painting0 = MoreCommands.cloneEntity(painting, false);
        painting.kill();
        painting0.setPosRaw(pos.getX(), pos.getY(), pos.getZ());
        ctx.getSource().getLevel().addFreshEntity(painting0);
        return 1;
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/cycle-painting";
    }
}
