package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class RealnameCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("realname").then(argument("query", StringArgumentType.word()).executes(ctx -> {
			String query = ctx.getArgument("query", String.class).toLowerCase();
			List<ServerPlayerEntity> players = new ArrayList<>();
			for (ServerPlayerEntity player : ctx.getSource().getMinecraftServer().getPlayerManager().getPlayerList())
				if (Formatting.strip(MoreCommands.textToString(player.getDataTracker().get(MoreCommands.NICKNAME).orElse(new LiteralText("")), null)).toLowerCase().contains(query) || MoreCommands.textToString(player.getName(), null).toLowerCase().contains(query))
					players.add(player);

			if (players.size() == 0) sendMsg(ctx, Formatting.RED + "No players whose name matches the given query were found.");
			else for (ServerPlayerEntity player : players)
				sendMsg(ctx, MoreCommands.textToString(player.getDisplayName(), SS) + DF + " is " + MoreCommands.textToString(player.getName(), SS));
			return players.size();
		})));
	}
}
