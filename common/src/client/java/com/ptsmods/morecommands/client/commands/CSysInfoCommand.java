package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.commands.elevated.SysInfoCommand;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class CSysInfoCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("csysinfo")
                .executes(ctx -> SysInfoCommand.sendSysInfo(null, ClientCommand::sendMsg)));
    }

    @Override
    public String getDocsPath() {
        return "/c-sys-info";
    }
}
