package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerConnectionCallback {
    Event<PlayerConnectionCallback> JOIN = EventFactory.createArrayBacked(PlayerConnectionCallback.class, callbacks -> player -> {
        for (PlayerConnectionCallback callback : callbacks) callback.call(player);
    });
    Event<PlayerConnectionCallback> LEAVE = EventFactory.createArrayBacked(PlayerConnectionCallback.class, callbacks -> player -> {
        for (PlayerConnectionCallback callback : callbacks) callback.call(player);
    });

    void call(ServerPlayerEntity player);
}
