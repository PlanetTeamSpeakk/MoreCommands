package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class DifficultyLiteralCommand extends Command {
	@SuppressWarnings("unchecked")
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().getChild("difficulty").getChildren().forEach(cmd -> dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>) cmd.createBuilder().requires(IS_OP)));
	}
}
