package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;

public class ToggleOptionCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<ClientCommandSource> toggleoption = cLiteral("toggleoption");

        ClientOption.getUnmappedOptions().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof BooleanClientOption)
                .forEach(entry -> toggleoption.then(cLiteral(entry.getValue().getKey())
                        .executes(ctx -> execute((BooleanClientOption) entry.getValue(), null))
                        .then(cArgument("value", BoolArgumentType.bool())
                                .executes(ctx -> execute((BooleanClientOption) entry.getValue(), ctx.getArgument("value", Boolean.class))))));

        dispatcher.register(toggleoption);
    }

    private static int execute(BooleanClientOption option, Boolean value) {
        boolean b = value == null ? !option.getValueRaw() : value;
        option.setValue(b);
        ClientOptions.write();
        sendMsg("Option %s has been set to %s.", coloured(option.getName()), Util.formatFromBool(b, "TRUE", "FALSE") + DF);
        return 1;
    }
}
