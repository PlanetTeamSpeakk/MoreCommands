package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface PostInitEvent {
    Event<PostInitEvent> EVENT = EventFactory.createLoop();

    void postInit();
}
