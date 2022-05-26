package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.Formatting;

public class CalcCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("calc")
                .then(cArgument("equation", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String equation = ctx.getArgument("equation", String.class);
                            double d;
                            try {
                                d = MoreCommands.eval(equation);
                            } catch (RuntimeException e) {
                                sendMsg(Formatting.RED + e.getMessage());
                                return 0;
                            }
                            sendMsg(SF + equation + DF + " = " + SF + d);
                            return (int) d;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/client/calc";
    }
}
