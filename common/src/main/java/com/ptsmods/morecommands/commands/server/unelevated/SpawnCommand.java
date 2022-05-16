package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class SpawnCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		dispatcher.register(literalReq("spawn").executes(ctx -> execute(ctx, null))
				.then(argument("player", EntityArgumentType.player()).requires(hasPermissionOrOp("morecommands.spawn.others")).executes(ctx -> execute(ctx, EntityArgumentType.getPlayer(ctx, "player")))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
		Entity entity = player == null ? ctx.getSource().getEntityOrThrow() : player;
		ServerWorld world = Objects.requireNonNull(ctx.getSource().getServer().getWorld(World.OVERWORLD));
		MoreCommands.teleport(entity, world, Vec3d.ofCenter(Compat.get().getWorldSpawnPos(world)), entity.getYaw(), entity.getPitch());
		if (player != null) sendMsg(player, literalText("You have been teleported to spawn by ", DS)
				.append(Compat.get().builderFromText(ctx.getSource().getDisplayName()))
				.append("."));
		return 1;
	}
}
