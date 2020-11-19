package com.ptsmods.morecommands.commands.client;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.Map;

public class ChelpCommand extends ClientCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.help.failed"));

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        // For just a few changes I have to copy the whole command. lulw
        dispatcher.register(cLiteral("chelp").executes((commandContext) -> {
            Map<CommandNode<ClientCommandSource>, String> map = dispatcher.getSmartUsage(dispatcher.getRoot(), commandContext.getSource());
            for (String string : map.values()) sendMsg(new LiteralText("/" + string));
            return map.size();
        }).then(cArgument("command", StringArgumentType.greedyString()).executes((commandContext) -> {
            ParseResults<ClientCommandSource> parseResults = dispatcher.parse(StringArgumentType.getString(commandContext, "command"), commandContext.getSource());
            if (parseResults.getContext().getNodes().isEmpty()) throw FAILED_EXCEPTION.create();
            else {
                Map<CommandNode<ClientCommandSource>, String> map = dispatcher.getSmartUsage(Iterables.getLast(parseResults.getContext().getNodes()).getNode(), commandContext.getSource());
                for (String string : map.values()) sendMsg(new LiteralText("/" + parseResults.getReader().getString() + " " + string));
                return map.size();
            }
        })));
    }
}
