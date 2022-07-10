package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ScoreCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("score")
                .executes(ctx -> {
                    sendMsg("Your score is currently " + SF + getPlayer().getScore() + DF + ".");
                    return getPlayer().getScore()+1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/score";
    }
}
