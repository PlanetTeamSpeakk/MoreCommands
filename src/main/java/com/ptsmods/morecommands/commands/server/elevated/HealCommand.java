package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class HealCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(MoreCommands.createAlias("feed", dispatcher.register(literalReqOp("heal").executes(ctx -> execute(ctx.getSource().getPlayer())).then(argument("players", EntityArgumentType.players()).executes(ctx -> {
			Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
			players.forEach(this::execute);
			sendMsg(ctx, "Healed " + SF + players.size() + DF + " players.");
			return players.size();
		})))));
	}

	private int execute(ServerPlayerEntity player) {
		player.setHealth(player.getMaxHealth());
		player.getHungerManager().add(20, 20);
		sendMsg(player, "You have been healed and fed.");
		return 1;
	}
}
