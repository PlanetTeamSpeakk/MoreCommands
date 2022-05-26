package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;

public class SendCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("send")
                .then(cArgument("msg", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            getPlayer().networkHandler.sendPacket(ClientCompat.get().newChatMessagePacket(getPlayer(), ctx.getArgument("msg", String.class), false));
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/client/send";
    }
}
