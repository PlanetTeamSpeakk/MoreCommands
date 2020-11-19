package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface KeyCallback {
    Event<KeyCallback> EVENT = EventFactory.createArrayBacked(KeyCallback.class, callbacks -> (key, scancode, action, mods) -> {
        for (KeyCallback callback : callbacks) if (callback.onKey(key, scancode, action, mods)) return true;
        return false;
    });

    boolean onKey(int key, int scancode, int action, int mods);

}
