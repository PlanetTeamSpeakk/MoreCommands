package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;

public class JumpCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("jump").executes(ctx -> {
			MoreCommands.teleport(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160d, true, true).getPos(), Compat.getCompat().getEntityYaw(Objects.requireNonNull(ctx.getSource().getEntity())), Compat.getCompat().getEntityPitch(ctx.getSource().getEntity()));
			return 1;
		}));
	}
}
