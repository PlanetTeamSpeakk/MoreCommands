package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

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
        if (entities.size() == 1 && !entities.iterator().next().isOnFire()) {
            sendMsg(ctx, "Confused tsss");
            return 0;
        }

        int extinguished = 0;
        for (Entity entity : entities)
            if (entity.isOnFire()) {
                entity.clearFire();
                entity.getCommandSenderWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 0.7F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                extinguished++;
            }

        sendMsg(ctx, extinguished > 0 ? "Tsss" : "Confused tsss");
        return extinguished;
    }
}
