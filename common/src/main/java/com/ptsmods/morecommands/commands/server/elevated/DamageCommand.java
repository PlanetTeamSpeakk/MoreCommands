package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageCommand extends Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        DamageSource.OUT_OF_WORLD.getMsgId(); // Making sure the class is instantiated.
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> entities = argument("entities", EntityArgument.entities());

        MoreCommands.DAMAGE_SOURCES.forEach((name, source) -> entities.then(literal(name)
                .then(argument("damage", FloatArgumentType.floatArg(1f))
                        .executes(ctx -> {
                            Collection<? extends Entity> entityList = EntityArgument.getEntities(ctx, "entities");
                            float damage = ctx.getArgument("damage", float.class);
                            entityList.forEach(entity -> entity.hurt(source, damage));

                            sendMsg(ctx, "Dealt " + SF + damage + DF + " damage to " + SF + entityList.size() + DF + " entities.");
                            return (int) (entityList.size() * damage);
                        }))));

        dispatcher.register(literalReqOp("damage").then(entities));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/damage";
    }
}
