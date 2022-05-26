package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Either;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class SmiteCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("smite")
                .executes(ctx -> execute(ctx, Either.left(MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160D, false, true))))
                .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> execute(ctx, Either.right(EntityArgumentType.getPlayer(ctx, "player"))))));
    }

    @Override
    public String getDocsPath() {
        return "/server/elevated/smite";
    }

    private int execute(CommandContext<ServerCommandSource> ctx, Either<HitResult, ServerPlayerEntity> hitOrPlayer) {
        Vec3d pos = hitOrPlayer.left().isPresent() ? hitOrPlayer.left().get().getPos() : hitOrPlayer.right().orElseThrow(NullPointerException::new).getPos();
        for (int i = 0; i < 3; i++) {
            LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT, ctx.getSource().getWorld());
            bolt.setPos(pos.getX(), pos.getY(), pos.getZ());
            ctx.getSource().getWorld().spawnEntity(bolt);
        }
        return 1;
    }
}
