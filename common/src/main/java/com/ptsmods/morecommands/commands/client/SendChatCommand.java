package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class SendChatCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception {
        if (!Version.getCurrent().isNewerThanOrEqual(Version.V1_19)) return;

        // Forcibly send any message as a chat message and not a command.
        dispatcher.register(cLiteral("sendchat")
                .then(cArgument("msg", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            getPlayer().connection.send(ClientCompat.get().newChatMessagePacket(getPlayer(), ctx.getArgument("msg", String.class), true));
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/send-chat";
    }
}
