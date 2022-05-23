package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;

public class DamageCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        DamageSource.OUT_OF_WORLD.getName(); // Making sure the class is instantiated.
        RequiredArgumentBuilder<ServerCommandSource, EntitySelector> entities = argument("entities", EntityArgumentType.entities());

        MoreCommands.DAMAGE_SOURCES.forEach((name, source) -> entities.then(literal(name)
                .then(argument("damage", FloatArgumentType.floatArg(1f))
                        .executes(ctx -> {
                            Collection<? extends Entity> entityList = EntityArgumentType.getEntities(ctx, "entities");
                            float damage = ctx.getArgument("damage", float.class);
                            entityList.forEach(entity -> entity.damage(source, damage));

                            sendMsg(ctx, "Dealt " + SF + damage + DF + " damage to " + SF + entityList.size() + DF + " entities.");
                            return (int) (entityList.size() * damage);
                        }))));

        dispatcher.register(literalReqOp("damage").then(entities));
    }
}
