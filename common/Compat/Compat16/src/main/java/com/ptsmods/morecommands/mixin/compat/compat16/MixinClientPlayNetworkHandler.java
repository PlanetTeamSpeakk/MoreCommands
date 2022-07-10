package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.IDeathTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPacketListener.class)
public class MixinClientPlayNetworkHandler {

    @Shadow private Minecraft minecraft;
    @Shadow private ClientLevel level;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V", shift = At.Shift.AFTER), method = "handlePlayerCombat")
    private void onCombatEvent(ClientboundPlayerCombatPacket packet, CallbackInfo cbi) {
        if (packet.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED && level.getEntity(packet.playerId) == minecraft.player)
            IDeathTracker.get().addDeath(level, Objects.requireNonNull(minecraft.player).position());
    }
}
