package com.ptsmods.morecommands.callbacks;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.command.ServerCommandSource;

public interface CommandsRegisteredCallback {
    Event<CommandsRegisteredCallback> EVENT = EventFactory.createArrayBacked(CommandsRegisteredCallback.class, callbacks -> dispatcher -> {
        for (CommandsRegisteredCallback callback : callbacks) callback.onRegistered(dispatcher);
    });

    void onRegistered(CommandDispatcher<ServerCommandSource> dispatcher);

}
