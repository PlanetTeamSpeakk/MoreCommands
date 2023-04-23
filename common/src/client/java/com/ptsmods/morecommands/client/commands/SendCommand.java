package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class SendCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("send")
                .then(cArgument("msg", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            getPlayer().connection.send(ClientCompat.get().newChatMessagePacket(getPlayer(), ctx.getArgument("msg", String.class), false));
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/send";
    }
}
