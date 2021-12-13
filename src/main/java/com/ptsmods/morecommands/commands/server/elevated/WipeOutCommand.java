package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WipeOutCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("wipeout").executes(ctx -> execute(ctx, e -> !(e instanceof PlayerEntity)))
                .then(literal("all").executes(ctx -> execute(ctx, e -> e != ctx.getSource().getEntity())))
                .then(literal("monsters").executes(ctx -> execute(ctx, e -> e instanceof Monster)))
                .then(literal("mobs").executes(ctx -> execute(ctx, e -> e instanceof MobEntity)))
                .then(literal("animals").executes(ctx -> execute(ctx, e -> e instanceof AnimalEntity)))
                .then(literal("living").executes(ctx -> execute(ctx, e -> e instanceof LivingEntity && !(e instanceof PlayerEntity))))
                .then(literal("player").executes(ctx -> execute(ctx, e -> e instanceof PlayerEntity)))
                .then(literal("other").executes(ctx -> execute(ctx, e -> !(e instanceof LivingEntity)))
        ));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, Predicate<Entity> predicate) {
        List<Entity> entities = StreamSupport.stream(ctx.getSource().getServer().getWorlds().spliterator(), false)
                .flatMap(world -> StreamSupport.stream(world.iterateEntities().spliterator(), false))
                .filter(predicate)
                .collect(Collectors.toList());
        entities.forEach(entity -> Compat.getCompat().setRemoved(entity, 1));
        sendMsg(ctx, SF.toString() + entities.size() + " " + DF + "entities have been killed.");
        return entities.size();
    }
}
