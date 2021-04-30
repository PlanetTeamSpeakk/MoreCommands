package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;

public class DimensionCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("dimension").requires(IS_OP)
				.then(argument("dimension", DimensionArgumentType.dimension()).executes(ctx -> execute(ctx, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), ctx.getSource().getEntityOrThrow()))
						.then(argument("player", EntityArgumentType.player()).executes(ctx -> execute(ctx, DimensionArgumentType.getDimensionArgument(ctx, "dimension"), EntityArgumentType.getPlayer(ctx, "player"))))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, ServerWorld world, Entity entity) {
		if (entity.getEntityWorld() != world) {
			MoreCommands.teleport(entity, world, entity.getX(), entity.getY(), entity.getZ(), Compat.getCompat().getEntityYaw(entity), Compat.getCompat().getEntityPitch(entity));
			return 1;
		}
		sendMsg(ctx, Formatting.RED + "The targeted entity is already in that world.");
		return 0;
	}

}
