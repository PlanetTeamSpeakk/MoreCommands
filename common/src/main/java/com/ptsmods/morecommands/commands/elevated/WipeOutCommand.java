package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WipeOutCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("wipeout")
                .executes(ctx -> execute(ctx, e -> !(e instanceof Player)))
                .then(literal("all")
                        .executes(ctx -> execute(ctx, e -> e != ctx.getSource().getEntity())))
                .then(literal("monsters")
                        .executes(ctx -> execute(ctx, e -> e instanceof Enemy)))
                .then(literal("mobs")
                        .executes(ctx -> execute(ctx, e -> e instanceof Mob)))
                .then(literal("animals")
                        .executes(ctx -> execute(ctx, e -> e instanceof Animal)))
                .then(literal("living")
                        .executes(ctx -> execute(ctx, e -> e instanceof LivingEntity && !(e instanceof Player))))
                .then(literal("player")
                        .executes(ctx -> execute(ctx, e -> e instanceof Player)))
                .then(literal("other")
                        .executes(ctx -> execute(ctx, e -> !(e instanceof LivingEntity)))
        ));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/wipe-out";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Predicate<Entity> predicate) {
        List<Entity> entities = StreamSupport.stream(ctx.getSource().getServer().getAllLevels().spliterator(), false)
                .flatMap(world -> StreamSupport.stream(world.getAllEntities().spliterator(), false))
                .filter(predicate)
                .collect(Collectors.toList());
        entities.forEach(entity -> entity.setRemoved(Entity.RemovalReason.DISCARDED));
        sendMsg(ctx, SF.toString() + entities.size() + " " + DF + "entities have been killed.");
        return entities.size();
    }
}
