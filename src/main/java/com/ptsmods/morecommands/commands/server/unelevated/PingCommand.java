package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class PingCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("ping").executes(ctx -> sendMsg(ctx, "Pong! " + ctx.getSource().getPlayer().pingMilliseconds + " ms latency")));
	}
}
