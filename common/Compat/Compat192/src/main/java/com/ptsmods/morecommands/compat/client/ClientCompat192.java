package com.ptsmods.morecommands.compat.client;

import com.ptsmods.morecommands.api.IMoreCommands;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;

import java.util.function.Function;

public class ClientCompat192 extends ClientCompat191 {

    @Override
    public void registerChatProcessListener(Function<String, String> listener) {
        ClientChatEvent.RECEIVED.register((bound, message) -> {
            String messageString = IMoreCommands.get().textToString(message, null, true);
            String output = listener.apply(messageString);

            if (output == null || output.equals(messageString)) return CompoundEventResult.pass();
            return CompoundEventResult.interruptDefault(message);
        });
    }
}
