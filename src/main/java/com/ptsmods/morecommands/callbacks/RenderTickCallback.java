package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RenderTickCallback {
	Event<RenderTickCallback> PRE = EventFactory.createArrayBacked(RenderTickCallback.class, callbacks -> tick -> {
		for (RenderTickCallback callback : callbacks)
			callback.render(tick);
	});
	Event<RenderTickCallback> POST = EventFactory.createArrayBacked(RenderTickCallback.class, callbacks -> tick -> {
		for (RenderTickCallback callback : callbacks)
			callback.render(tick);
	});

	void render(boolean tick);
}
