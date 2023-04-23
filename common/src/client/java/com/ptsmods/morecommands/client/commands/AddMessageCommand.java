package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class AddMessageCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("addmsg")
                .redirect(dispatcher.register(cLiteral("addmessage")
                        .then(cArgument("msg", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Minecraft.getInstance().gui.getChat().addMessage(literalText(MoreCommands.translateFormattings(ctx.getArgument("msg", String.class))).build());
                                    return 1;
                                })))));
    }

    @Override
    public String getDocsPath() {
        return "/add-message";
    }
}
