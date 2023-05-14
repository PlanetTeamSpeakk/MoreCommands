package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public interface PacketReceiveEvent {
    Event<PacketReceiveEvent.Pre> PRE = EventFactory.of(callbacks -> (packet, listener) -> {
        for (PacketReceiveEvent.Pre callback : callbacks)
            if (callback.onReceive(packet, listener))
                return true;
        return false;
    });
    Event<PacketReceiveEvent.Post> POST = EventFactory.of(callbacks -> (packet, listener) -> {
        for (PacketReceiveEvent.Post callback : callbacks)
            callback.onReceive(packet, listener);
    });

    interface Pre {
        boolean onReceive(Packet<?> packet, PacketListener listener);
    }

    interface Post {
        void onReceive(Packet<?> packet, PacketListener listener);
    }
}
