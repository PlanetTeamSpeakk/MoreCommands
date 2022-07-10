package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinLivingEntityAccessor;
import java.util.Collection;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RemoveCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("remove")
                .executes(ctx -> execute(ctx.getSource(), ImmutableList.of((ctx.getSource()).getEntityOrException())))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(ctx -> execute(ctx.getSource(), EntityArgument.getEntities(ctx, "targets")))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/remove";
    }

    private static int execute(CommandSourceStack source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        targets = targets.stream()
                .filter(e -> !(e instanceof Player)) // Don't remove players
                .collect(Collectors.toList());

        for (Entity entity : targets) {
            Compat.get().setRemoved(entity, 1);
            if (entity instanceof LivingEntity)
                ((MixinLivingEntityAccessor) entity).setDead(true);
        }

        sendMsg(source.getEntityOrException(), targets.size() == 1 ? translatableText("commands.kill.success.single", targets.iterator().next().getDisplayName()).withStyle(DS) :
                translatableText("commands.kill.success.multiple", targets.size()).withStyle(DS));
        return targets.size();
    }
}
