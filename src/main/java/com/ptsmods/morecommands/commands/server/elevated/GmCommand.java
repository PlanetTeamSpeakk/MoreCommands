package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GmCommand extends Command {
	private final Field literalField = ReflectionHelper.getField(LiteralArgumentBuilder.class, "literal");

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		CommandNode<ServerCommandSource> gamemode = dispatcher.getRoot().getChild("gamemode");
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("creative")).createBuilder(), "gmc").requires(IS_OP));
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("survival")).createBuilder(), "gms").requires(IS_OP));
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("adventure")).createBuilder(), "gma").requires(IS_OP));
		dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("spectator")).createBuilder(), "gmsp").requires(IS_OP));
	}

	private LiteralArgumentBuilder<ServerCommandSource> setLiteral(LiteralArgumentBuilder<ServerCommandSource> builder, String literal) {
		return literal(literal).requires(builder.getRequirement()).forward(builder.getRedirect(), builder.getRedirectModifier(), builder.isFork()).executes(builder.getCommand());
	}
}
