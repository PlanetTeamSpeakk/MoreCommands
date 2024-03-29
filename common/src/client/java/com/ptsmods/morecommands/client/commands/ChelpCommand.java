package com.ptsmods.morecommands.client.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ChelpCommand extends ClientCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(translatableText("commands.help.failed").build());

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        // For just a few changes I have to copy the whole command. lulw
        dispatcher.register(cLiteral("chelp")
                .executes(ctx -> {
                    Map<CommandNode<ClientSuggestionProvider>, String> map = dispatcher.getSmartUsage(dispatcher.getRoot(), ctx.getSource());
                    for (String string : map.values()) sendMsg(literalText("/" + string));
                    return map.size();
                })
                .then(cArgument("command", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ParseResults<ClientSuggestionProvider> parseResults = dispatcher.parse(StringArgumentType.getString(ctx, "command"), ctx.getSource());
                            if (parseResults.getContext().getNodes().isEmpty()) throw FAILED_EXCEPTION.create();
                            else {
                                Map<CommandNode<ClientSuggestionProvider>, String> map = dispatcher.getSmartUsage(Iterables.getLast(parseResults.getContext().getNodes()).getNode(), ctx.getSource());
                                for (String string : map.values()) sendMsg(literalText("/" + parseResults.getReader().getString() + " " + string));
                                return map.size();
                            }
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/c-help";
    }
}
