package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface CreateWorldsCallback {
	Event<CreateWorldsCallback> EVENT = EventFactory.createArrayBacked(CreateWorldsCallback.class, callbacks -> server -> {
		for (CreateWorldsCallback callback : callbacks)
			callback.createWorlds(server);
	});

	void createWorlds(MinecraftServer server);
}
