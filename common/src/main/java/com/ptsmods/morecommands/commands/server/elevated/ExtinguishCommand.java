package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtinguishCommand extends Command {

    private static final Random rand = new Random();

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literalReqOp("extinguish")
                        .executes(ctx -> execute(ctx, null))
                        .then(argument("targets", EntityArgumentType.entities())
                                .executes(ctx -> execute(ctx, EntityArgumentType.getEntities(ctx, "targets")))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, Collection<? extends Entity> entities) throws CommandSyntaxException {
        if (entities == null) entities = Collections.singletonList(ctx.getSource().getEntityOrThrow());
        AtomicInteger success = new AtomicInteger();
        for (Entity entity : entities)
            if (entity.isOnFire()) {
                entity.extinguish();
                entity.getEntityWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 0.7F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                success.incrementAndGet();
            } else if (entities.size() == 1) sendMsg(ctx, "Confused tsss");
        sendMsg(ctx, success.get() > 0 ? "Tsss" : "Confused tss");
        return success.get();
    }

}
