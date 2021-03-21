package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.arguments.LimitedStringArgumentType;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.network.ClientCommandSource;

import java.util.List;
import java.util.stream.Collectors;

public class ToggleOptionCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        List<String> options = ClientOptions.getFieldNames().stream().filter(option -> ClientOptions.getType(option) == Boolean.class).collect(Collectors.toList());
        dispatcher.register(cLiteral("toggleoption").then(cArgument("option", LimitedStringArgumentType.word(options)).executes(ctx -> {
            String option = ctx.getArgument("option", String.class);
            boolean b = !Boolean.parseBoolean(ClientOptions.getOptionString(option));
            ClientOptions.setOption(option, b);
            sendMsg("The option " + SF + option + DF + " has been set to " + formatFromBool(b, "TRUE", "FALSE") + DF + ".");
            return 1;
        })));
    }
}
