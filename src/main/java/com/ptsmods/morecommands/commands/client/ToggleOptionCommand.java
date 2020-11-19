package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.arguments.LimitedStringArgumentType;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.network.ClientCommandSource;

import java.util.ArrayList;
import java.util.List;

public class ToggleOptionCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        List<String> options = new ArrayList<>();
        ClientOptions.getOptions().forEach(s -> {
            if (ClientOptions.getType(s) == boolean.class)
                options.add(s);
        });
        dispatcher.register(cLiteral("toggleoption").then(cArgument("option", LimitedStringArgumentType.word(options)).executes(ctx -> {
            String option = ctx.getArgument("option", String.class);
            boolean b = !Boolean.parseBoolean(ClientOptions.getOption(option));
            ClientOptions.setOption(option, b);
            sendMsg("The option " + SF + option + DF + " has been set to " + formatFromBool(b, "TRUE", "FALSE") + DF + ".");
            return 1;
        })));
    }
}
