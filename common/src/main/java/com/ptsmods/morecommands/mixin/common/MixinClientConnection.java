package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.callbacks.PacketReceiveEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

	@Inject(at = @At("TAIL"), method = "handlePacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;)V", cancellable = true)
	private static <T extends PacketListener> void handlePacketPre(Packet<T> packet, PacketListener listener, CallbackInfo cbi) {
		if (PacketReceiveEvent.PRE.invoker().onReceive(packet, listener)) cbi.cancel();
	}

	@Inject(at = @At("TAIL"), method = "handlePacket(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;)V")
	private static <T extends PacketListener> void handlePacketPost(Packet<T> packet, PacketListener listener, CallbackInfo cbi) {
		PacketReceiveEvent.POST.invoker().onReceive(packet, listener);
	}

}
