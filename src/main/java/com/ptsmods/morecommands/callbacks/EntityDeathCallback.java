package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public interface EntityDeathCallback {
	Event<EntityDeathCallback> EVENT = EventFactory.createArrayBacked(EntityDeathCallback.class, callbacks -> entity -> {
		for (EntityDeathCallback callback : callbacks) callback.onDeath(entity);
	});

	void onDeath(Entity entity);
}
