package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

public class AddMessageCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("addmsg")
                .redirect(dispatcher.register(cLiteral("addmessage")
                        .then(cArgument("msg", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(literalText(MoreCommands.translateFormattings(ctx.getArgument("msg", String.class))).build());
                                    return 1;
                                })))));
    }
}
