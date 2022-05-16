package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface KeyEvent {
	Event<KeyEvent> EVENT = EventFactory.of(listeners -> (key, scancode, action, mods) -> {
		for (KeyEvent listener : listeners) if (listener.onKey(key, scancode, action, mods)) return true;
		return false;
	});

	boolean onKey(int key, int scancode, int action, int mods);

}
