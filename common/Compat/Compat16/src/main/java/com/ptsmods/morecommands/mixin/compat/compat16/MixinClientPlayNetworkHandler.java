package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.IDeathTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow private MinecraftClient client;
    @Shadow private ClientWorld world;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), method = "onCombatEvent")
    private void onCombatEvent(CombatEventS2CPacket packet, CallbackInfo cbi) {
        if (packet.type == CombatEventS2CPacket.Type.ENTITY_DIED && world.getEntityById(packet.entityId) == client.player)
            IDeathTracker.get().addDeath(world, Objects.requireNonNull(client.player).getPos());
    }
}
