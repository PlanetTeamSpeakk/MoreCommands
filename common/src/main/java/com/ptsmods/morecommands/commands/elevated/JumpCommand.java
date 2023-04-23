package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class JumpCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("jump")
                .executes(ctx -> {
                    Entity entity = ctx.getSource().getEntityOrException();
                    Vec3 velocity = entity.getDeltaMovement();

                    MoreCommands.teleport(entity, ctx.getSource().getLevel(),
                            MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160d, true, true).getLocation(),
                            ((MixinEntityAccessor) Objects.requireNonNull(ctx.getSource().getEntity())).getYRot_(), ((MixinEntityAccessor) ctx.getSource().getEntity()).getXRot_());

                    entity.setDeltaMovement(velocity);
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/jump";
    }
}
