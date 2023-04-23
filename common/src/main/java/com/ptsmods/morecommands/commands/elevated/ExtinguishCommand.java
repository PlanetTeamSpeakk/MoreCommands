package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class ExtinguishCommand extends Command {
    private static final Random rand = new Random();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literalReqOp("extinguish")
                        .executes(ctx -> execute(ctx, null))
                        .then(argument("targets", EntityArgument.entities())
                                .executes(ctx -> execute(ctx, EntityArgument.getEntities(ctx, "targets")))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/extinguish";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Collection<? extends Entity> entities) throws CommandSyntaxException {
        if (entities == null) entities = Collections.singletonList(ctx.getSource().getEntityOrException());
        AtomicInteger success = new AtomicInteger();
        for (Entity entity : entities)
            if (entity.isOnFire()) {
                entity.clearFire();
                entity.getCommandSenderWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 0.7F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                success.incrementAndGet();
            } else if (entities.size() == 1) sendMsg(ctx, "Confused tsss");
        sendMsg(ctx, success.get() > 0 ? "Tsss" : "Confused tss");
        return success.get();
    }

}
