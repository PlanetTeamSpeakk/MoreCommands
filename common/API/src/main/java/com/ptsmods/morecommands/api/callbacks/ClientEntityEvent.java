package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public interface ClientEntityEvent {
    Event<ClientEntityEvent> ENTITY_LOAD = EventFactory.createLoop();
    Event<ClientEntityEvent> ENTITY_UNLOAD = EventFactory.createLoop();

    void onEntity(ClientWorld world, Entity entity);
}
