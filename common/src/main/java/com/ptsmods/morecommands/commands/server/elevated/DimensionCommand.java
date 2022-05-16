package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class DimensionCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("dimension")
				.then(argument("dimension", DimensionArgumentType.dimension()).executes(ctx -> execute(ctx, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), ctx.getSource().getEntityOrThrow()))
						.then(argument("player", EntityArgumentType.player()).executes(ctx -> execute(ctx, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), EntityArgumentType.getPlayer(ctx, "player"))))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, ServerWorld world, Entity entity) {
		if (entity.getEntityWorld() != world) {
			MoreCommands.teleport(entity, world, entity.getX(), entity.getY(), entity.getZ(), ((MixinEntityAccessor) entity).getYaw_(), ((MixinEntityAccessor) entity).getPitch_());
			return 1;
		}
		sendError(ctx, "The targeted entity is already in that world.");
		return 0;
	}
}
