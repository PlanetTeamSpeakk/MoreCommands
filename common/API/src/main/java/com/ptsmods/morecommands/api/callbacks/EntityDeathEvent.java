package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.entity.Entity;

public interface EntityDeathEvent {
	Event<EntityDeathEvent> EVENT = EventFactory.createLoop();

	void onDeath(Entity entity);
}
