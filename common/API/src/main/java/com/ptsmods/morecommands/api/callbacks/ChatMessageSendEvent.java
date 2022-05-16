package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ChatMessageSendEvent {
	Event<ChatMessageSendEvent> EVENT = EventFactory.of(callbacks -> message -> {
		for (ChatMessageSendEvent callback : callbacks) message = callback.onMessageSend(message);
		return message;
	});

	String onMessageSend(String message);
}
