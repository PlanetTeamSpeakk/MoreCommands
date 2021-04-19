package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.arguments.RegistryArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class CyclePaintingCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("cyclepainting").executes(ctx -> execute(ctx, null)).then(argument("motive", new RegistryArgumentType<>(Registry.PAINTING_MOTIVE)).executes(ctx -> execute(ctx, ctx.getArgument("motive", PaintingMotive.class)))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, PaintingMotive motive) throws CommandSyntaxException {
		HitResult result = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160F, false, true);
		if (result.getType() == HitResult.Type.ENTITY && ((EntityHitResult) result).getEntity() instanceof PaintingEntity) {
			PaintingEntity painting = (PaintingEntity) ((EntityHitResult) result).getEntity();
			PaintingMotive oldArt = painting.motive;
			painting.motive = motive == null ? Registry.PAINTING_MOTIVE.get((Registry.PAINTING_MOTIVE.getRawId(oldArt)+1) % 26) : motive;
			BlockPos pos = painting.getBlockPos();
			Entity painting0 = MoreCommands.cloneEntity(painting, false);
			painting.kill();
			painting0.setPos(pos.getX(), pos.getY(), pos.getZ());
			ctx.getSource().getWorld().spawnEntity(painting0);
			return 1;
		} else sendMsg(ctx, Formatting.RED + "It appears as if you're not looking at a painting.");
		return 0;
	}

}
