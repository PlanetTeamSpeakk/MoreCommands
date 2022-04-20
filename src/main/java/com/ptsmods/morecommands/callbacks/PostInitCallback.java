package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PostInitCallback {
	Event<PostInitCallback> EVENT = EventFactory.createArrayBacked(PostInitCallback.class, callbacks -> () -> {
		for (PostInitCallback callback : callbacks) callback.postInit();
	});

	void postInit();
}
