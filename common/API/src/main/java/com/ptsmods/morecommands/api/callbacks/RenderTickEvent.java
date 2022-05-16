package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface RenderTickEvent {
	Event<RenderTickEvent> PRE = EventFactory.createLoop();
	Event<RenderTickEvent> POST = EventFactory.createLoop();

	void render(boolean tick);
}
