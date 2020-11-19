package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;

public class CalcCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("calc").then(cArgument("equation", StringArgumentType.greedyString()).executes(ctx -> {
            String equation = ctx.getArgument("equation", String.class);
            double d = MoreCommands.eval(equation);
            sendMsg(SF + equation + DF + " = " + SF + d);
            return (int) d;
        })));
    }
}
