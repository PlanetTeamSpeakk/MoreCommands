package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface MouseEvent {
	Event<MouseEvent> EVENT = EventFactory.of(callbacks -> (button, action, mods) -> {
		for (MouseEvent callback : callbacks) if (callback.onMouse(button, action, mods)) return true;
		return false;
	});

	boolean onMouse(int button, int action, int mods);
}
