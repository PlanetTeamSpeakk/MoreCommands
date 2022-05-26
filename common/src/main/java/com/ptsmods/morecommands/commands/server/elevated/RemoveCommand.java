package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinLivingEntityAccessor;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;
import java.util.stream.Collectors;

public class RemoveCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("remove")
                .executes(ctx -> execute(ctx.getSource(), ImmutableList.of((ctx.getSource()).getEntityOrThrow())))
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .executes(ctx -> execute(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets")))));
    }

    @Override
    public String getDocsPath() {
        return "/server/elevated/remove";
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        targets = targets.stream()
                .filter(e -> !(e instanceof PlayerEntity)) // Don't remove players
                .collect(Collectors.toList());

        for (Entity entity : targets) {
            Compat.get().setRemoved(entity, 1);
            if (entity instanceof LivingEntity)
                ((MixinLivingEntityAccessor) entity).setDead(true);
        }

        sendMsg(source.getEntityOrThrow(), targets.size() == 1 ? translatableText("commands.kill.success.single", targets.iterator().next().getDisplayName()).withStyle(DS) :
                translatableText("commands.kill.success.multiple", targets.size()).withStyle(DS));
        return targets.size();
    }
}
