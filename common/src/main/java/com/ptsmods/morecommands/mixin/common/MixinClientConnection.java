package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.callbacks.PacketReceiveEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class MixinClientConnection {

    @Inject(at = @At("TAIL"), method = "genericsFtw", cancellable = true)
    private static <T extends PacketListener> void handlePacketPre(Packet<T> packet, PacketListener listener, CallbackInfo cbi) {
        if (PacketReceiveEvent.PRE.invoker().onReceive(packet, listener)) cbi.cancel();
    }

    @Inject(at = @At("TAIL"), method = "genericsFtw")
    private static <T extends PacketListener> void handlePacketPost(Packet<T> packet, PacketListener listener, CallbackInfo cbi) {
        PacketReceiveEvent.POST.invoker().onReceive(packet, listener);
    }
}
