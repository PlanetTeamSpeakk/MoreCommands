package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class JumpCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("jump").executes(ctx -> {
			Entity entity = ctx.getSource().getEntityOrThrow();
			Vec3d velocity = entity.getVelocity();

			MoreCommands.teleport(entity, ctx.getSource().getWorld(),
					MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160d, true, true).getPos(),
					((MixinEntityAccessor) Objects.requireNonNull(ctx.getSource().getEntity())).getYaw_(), ((MixinEntityAccessor) ctx.getSource().getEntity()).getPitch_());

			entity.setVelocity(velocity);
			return 1;
		}));
	}
}
