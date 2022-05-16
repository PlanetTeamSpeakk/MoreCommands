package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class GmCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		CommandNode<ServerCommandSource> gamemode = dispatcher.getRoot().getChild("gamemode");
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("creative")).createBuilder(), "gmc").requires(IS_OP));
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("survival")).createBuilder(), "gms").requires(IS_OP));
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("adventure")).createBuilder(), "gma").requires(IS_OP));
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("spectator")).createBuilder(), "gmsp").requires(IS_OP));
	}

	private LiteralArgumentBuilder<ServerCommandSource> setLiteral(LiteralArgumentBuilder<ServerCommandSource> builder, String literal) {
		LiteralArgumentBuilder<ServerCommandSource> cmd = literal(literal).requires(hasPermissionOrOp("morecommands." + literal)).forward(builder.getRedirect(), builder.getRedirectModifier(), builder.isFork()).executes(builder.getCommand());
		builder.build().getChildren().forEach(child -> cmd.then(child.createBuilder()));
		return cmd;
	}
}
