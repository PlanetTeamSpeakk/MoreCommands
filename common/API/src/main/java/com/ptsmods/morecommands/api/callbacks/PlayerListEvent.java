package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.multiplayer.PlayerInfo;

public interface PlayerListEvent {
    Event<PlayerListEvent> ADD = EventFactory.createLoop();
    Event<PlayerListEvent> REMOVE = EventFactory.createLoop();

    void call(PlayerInfo entry);
}
