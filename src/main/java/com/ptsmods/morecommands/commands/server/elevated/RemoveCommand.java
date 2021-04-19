package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinLivingEntityAccessor;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

public class RemoveCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		dispatcher.register(literal("remove").requires(IS_OP).executes(ctx -> execute(ctx.getSource(), ImmutableList.of((ctx.getSource()).getEntityOrThrow()))).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes(ctx -> execute(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets")))));
	}

	private static int execute(ServerCommandSource source, Collection<? extends Entity> targets) {
		for (Entity entity : targets) {
			entity.removed = true;
			if (entity instanceof LivingEntity)
				((MixinLivingEntityAccessor) entity).setDead(true);
		}
		if (targets.size() == 1) sendMsg(source.getEntity(), new TranslatableText("commands.kill.success.single", targets.iterator().next().getDisplayName()).setStyle(DS));
		else sendMsg(source.getEntity(), new TranslatableText("commands.kill.success.multiple", targets.size()).setStyle(DS));
		return targets.size();
	}
}
