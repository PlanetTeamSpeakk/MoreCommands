package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class CalcCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("calc")
                .then(cArgument("equation", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String equation = ctx.getArgument("equation", String.class);
                            double d;
                            try {
                                d = MoreCommands.eval(equation);
                            } catch (RuntimeException e) {
                                sendMsg(ChatFormatting.RED + e.getMessage());
                                return 0;
                            }
                            sendMsg(SF + equation + DF + " = " + SF + d);
                            return (int) d;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/calc";
    }
}
