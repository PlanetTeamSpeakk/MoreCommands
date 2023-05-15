package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.callbacks.PacketReceiveEvent;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketUtils.class)
public class MixinPacketUtils {

    @Inject(method = "method_11072", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"), cancellable = true)
    private static void firePacketReceiveEventPre(PacketListener packetListener, Packet<?> packet, CallbackInfo ci) {
        if (PacketReceiveEvent.PRE.invoker().onReceive(packet, packetListener)) ci.cancel();
    }

    @Inject(method = "method_11072", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V", shift = At.Shift.AFTER))
    private static void firePacketReceiveEventPost(PacketListener packetListener, Packet<?> packet, CallbackInfo ci) {
        PacketReceiveEvent.POST.invoker().onReceive(packet, packetListener);
    }
}
