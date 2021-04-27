package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.PlayerListEntry;

public interface PlayerListCallback {
	Event<PlayerListCallback> ADD = EventFactory.createArrayBacked(PlayerListCallback.class, callbacks -> entry -> {
		for (PlayerListCallback callback : callbacks) callback.call(entry);
	});
	Event<PlayerListCallback> REMOVE = EventFactory.createArrayBacked(PlayerListCallback.class, callbacks -> entry -> {
		for (PlayerListCallback callback : callbacks) callback.call(entry);
	});

	void call(PlayerListEntry entry);
}
