package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.server.command.ServerCommandSource;

public class SuicideCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReq("suicide").executes(ctx -> {
			ctx.getSource().getEntityOrThrow().damage(new EntityDamageSource("suicide", ctx.getSource().getEntity()), Float.MAX_VALUE);
			ctx.getSource().getEntityOrThrow().kill();
			return 1;
		}));
	}
}
