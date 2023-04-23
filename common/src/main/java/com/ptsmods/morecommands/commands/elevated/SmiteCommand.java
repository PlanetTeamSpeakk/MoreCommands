package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Either;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SmiteCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("smite")
                .executes(ctx -> execute(ctx, Either.left(MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160D, false, true))))
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> execute(ctx, Either.right(EntityArgument.getPlayer(ctx, "player"))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/smite";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Either<HitResult, ServerPlayer> hitOrPlayer) {
        Vec3 pos = hitOrPlayer.left().isPresent() ? hitOrPlayer.left().get().getLocation() : hitOrPlayer.right().orElseThrow(NullPointerException::new).position();
        for (int i = 0; i < 3; i++) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, ctx.getSource().getLevel());
            bolt.setPosRaw(pos.x(), pos.y(), pos.z());
            ctx.getSource().getLevel().addFreshEntity(bolt);
        }
        return 1;
    }
}
