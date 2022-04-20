package com.ptsmods.morecommands.callbacks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public interface ChatMessageSendCallback {
	Event<ChatMessageSendCallback> EVENT = EventFactory.createArrayBacked(ChatMessageSendCallback.class, callbacks -> message -> {
		for (ChatMessageSendCallback callback : callbacks) message = callback.onMessageSend(message);
		return message;
	});

	String onMessageSend(String message);
}
