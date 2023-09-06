package com.ptsmods.morecommands.commands.elevated;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class JumpCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("jump")
                .executes(ctx -> {
                    Entity entity = ctx.getSource().getEntityOrException();
                    Vec3 tpTarget = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160d,
                            true, true, true).getLocation();
                    float yRot = ((MixinEntityAccessor) Objects.requireNonNull(ctx.getSource().getEntity())).getYRot_();
                    float xRot = ((MixinEntityAccessor) ctx.getSource().getEntity()).getXRot_();

                    if (entity instanceof ServerPlayer player) {
                        // Ensure players keep their velocity
                        ChunkPos chunkPos = new ChunkPos(new BlockPos((int) tpTarget.x, (int) tpTarget.y, (int) tpTarget.z));
                        player.getLevel().getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, player.getId());
                        player.stopRiding();

                        player.connection.teleport(tpTarget.x, tpTarget.y, tpTarget.z, yRot, xRot,
                                Sets.newHashSet(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z));
                    } else {
                        Vec3 velocity = entity.getDeltaMovement();

                        MoreCommands.teleport(entity, ctx.getSource().getLevel(),
                                tpTarget, yRot, xRot);

                        entity.setDeltaMovement(velocity);
                        entity.hurtMarked = true;
                    }
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/jump";
    }
}
