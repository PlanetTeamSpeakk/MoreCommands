package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow private ClientWorld world;

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), method = "onDeathMessage")
    private void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo cbi) {
        IMoreCommands.LOG.info("Death at " + client.player.getPos());
        if (world.getEntityById(packet.getEntityId()) == client.player) IDeathTracker.get().addDeath(world, Objects.requireNonNull(client.player).getPos());
    }
}
