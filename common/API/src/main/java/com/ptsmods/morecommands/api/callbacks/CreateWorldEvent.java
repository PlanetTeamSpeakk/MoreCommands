package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface CreateWorldEvent {
	Event<CreateWorldEvent> EVENT = EventFactory.createLoop();

	void createWorlds(MinecraftServer server);
}
