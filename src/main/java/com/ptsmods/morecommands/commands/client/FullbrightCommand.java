package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

public class FullbrightCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("fullbright").executes(ctx -> {
            MinecraftClient.getInstance().options.gamma = Short.MAX_VALUE;
            MinecraftClient.getInstance().options.write();
            sendMsg("You can now see everything!");
            return 1;
        }));
    }
}
