package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

public class GameRuleDescCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		if (IMoreCommands.get().isServerOnly()) return; // No lang files on client-side in server-only mode.
		LiteralArgumentBuilder<ServerCommandSource> gamerule = literal("gamerule");
		GameRules.accept(new GameRules.Visitor() {
			public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				gamerule.then(literal(key.getName()).then(literal("desc").executes(ctx -> {
					sendMsg(ctx, literalText("Description for gamerule " + key.getName() + ": ").append(translatableText(key.getTranslationKey() + ".description")));
					return 1;
				})));
			}
		});
		dispatcher.register(gamerule);
	}
}
