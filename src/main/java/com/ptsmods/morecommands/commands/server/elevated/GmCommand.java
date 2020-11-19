package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GmCommand extends Command {

    private Field literalField = null;

    private void init() throws Exception {
        literalField = LiteralArgumentBuilder.class.getDeclaredField("literal");
        literalField.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.set(literalField, literalField.getModifiers() & ~Modifier.FINAL);
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        CommandNode<ServerCommandSource> gamemode = dispatcher.getRoot().getChild("gamemode");
        dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("creative")).createBuilder(), "gmc").requires(IS_OP));
        dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("survival")).createBuilder(), "gms").requires(IS_OP));
        dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("adventure")).createBuilder(), "gma").requires(IS_OP));
        dispatcher.register(setLiteral(((LiteralCommandNode<ServerCommandSource>) gamemode.getChild("spectator")).createBuilder(), "gmsp").requires(IS_OP));
    }

    private LiteralArgumentBuilder<ServerCommandSource> setLiteral(LiteralArgumentBuilder<ServerCommandSource> builder, String literal) {
        try {
            literalField.set(builder, literal);
        } catch (IllegalAccessException e) {
            log.catching(e);
        }
        return builder;
    }

}
