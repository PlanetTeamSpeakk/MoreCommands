package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

public interface PacketReceiveEvent {
    Event<PacketReceiveEvent> PRE = EventFactory.of(callbacks -> (packet, listener) -> {
        for (PacketReceiveEvent callback : callbacks)
            if (callback.onReceive(packet, listener))
                return true;
        return false;
    });
    Event<PacketReceiveEvent> POST = EventFactory.of(callbacks -> (packet, listener) -> {
        for (PacketReceiveEvent callback : callbacks)
            if (callback.onReceive(packet, listener))
                return true;
        return false;
    });

    boolean onReceive(Packet<?> packet, PacketListener listener);

}
