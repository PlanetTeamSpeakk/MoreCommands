package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.commands.server.elevated.SysInfoCommand;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class CSysInfoCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("csysinfo")
                .executes(ctx -> SysInfoCommand.sendSysInfo(null)));
    }

    @Override
    public String getDocsPath() {
        return "/c-sys-info";
    }
}
