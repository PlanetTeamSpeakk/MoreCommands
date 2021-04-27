package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MouseCallback {
	Event<MouseCallback> EVENT = EventFactory.createArrayBacked(MouseCallback.class, callbacks -> (button, action, mods) -> {
		for (MouseCallback callback : callbacks) if (callback.onMouse(button, action, mods)) return true;
		return false;
	});

	boolean onMouse(int button, int action, int mods);

}
