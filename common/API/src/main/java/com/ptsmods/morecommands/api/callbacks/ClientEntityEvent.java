package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public interface ClientEntityEvent {
    Event<ClientEntityEvent> ENTITY_LOAD = EventFactory.createLoop();
    Event<ClientEntityEvent> ENTITY_UNLOAD = EventFactory.createLoop();

    void onEntity(ClientLevel world, Entity entity);
}
