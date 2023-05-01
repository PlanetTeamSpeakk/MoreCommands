package com.ptsmods.morecommands.compat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class DamageCommand {

    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        IMoreCommands.get().registerPermission("morecommands.damage", false);

        DamageSource.OUT_OF_WORLD.getMsgId(); // Making sure the class is instantiated.
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> targets = Commands.argument("targets", EntityArgument.entities());

        IMoreCommands mc = IMoreCommands.get();
        IMoreCommands.DAMAGE_SOURCES.forEach((name, source) -> targets.then(Commands.literal(name)
                .then(Commands.argument("damage", FloatArgumentType.floatArg(1f))
                        .executes(ctx -> {
                            Collection<? extends Entity> entityList = EntityArgument.getEntities(ctx, "entities");
                            float damage = ctx.getArgument("damage", float.class);
                            entityList.forEach(entity -> entity.hurt(source, damage));

                            ctx.getSource().sendSuccess(LiteralTextBuilder.literal(mc.getDefaultFormatting() + "Dealt " +
                                    mc.getSecondaryFormatting() + damage + mc.getDefaultFormatting() + " damage to " +
                                    mc.getSecondaryFormatting() + entityList.size() + mc.getDefaultFormatting() + " entities."), true);
                            return (int) (entityList.size() * damage);
                        }))));

        dispatcher.register(Commands.literal("damage")
                .requires(MoreCommandsArch.isFabricModLoaded("fabric-permissions-api-v0") ?
                        MoreCommandsArch.requirePermission("morecommands.damage", 2) :
                        source -> source.hasPermission(2))
                .then(targets));
    }
}
