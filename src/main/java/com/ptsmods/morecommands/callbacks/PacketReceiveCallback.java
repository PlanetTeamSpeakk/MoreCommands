package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

public interface PacketReceiveCallback {
    Event<PacketReceiveCallback> PRE = EventFactory.createArrayBacked(PacketReceiveCallback.class, callbacks -> (packet, listener) -> {
        for (PacketReceiveCallback callback : callbacks)
            if (callback.onReceive(packet, listener))
                return true;
        return false;
    });
    Event<PacketReceiveCallback> POST = EventFactory.createArrayBacked(PacketReceiveCallback.class, callbacks -> (packet, listener) -> {
        for (PacketReceiveCallback callback : callbacks)
            if (callback.onReceive(packet, listener))
                return true;
        return false;
    });

    boolean onReceive(Packet<?> packet, PacketListener listener);

}
