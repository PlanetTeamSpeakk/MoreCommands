package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ToggleOptionCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        LiteralArgumentBuilder<ClientSuggestionProvider> toggleoption = cLiteral("toggleoption");

        ClientOption.getUnmappedOptions().values().stream()
                .filter(option -> option instanceof BooleanClientOption)
                .map(option -> (BooleanClientOption) option)
                .forEach(option -> toggleoption.then(cLiteral(option.getKey())
                        .executes(ctx -> execute(option, null))
                        .then(cArgument("value", BoolArgumentType.bool())
                                .executes(ctx -> execute(option, ctx.getArgument("value", Boolean.class))))));

        dispatcher.register(toggleoption);
    }

    @Override
    public String getDocsPath() {
        return "/toggle-option";
    }

    private static int execute(BooleanClientOption option, Boolean value) {
        boolean b = value == null ? !option.getValueRaw() : value;
        option.setValue(b);
        ClientOptions.write();
        sendMsg("Option %s has been set to %s.", coloured(option.getName()), Util.formatFromBool(b, "TRUE", "FALSE") + DF);
        return 1;
    }
}
