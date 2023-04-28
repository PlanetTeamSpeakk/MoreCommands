package com.ptsmods.morecommands.client.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.api.callbacks.ClientCommandRegistrationEvent;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.client.commands.PtimeCommand;
import com.ptsmods.morecommands.client.commands.PweatherCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
    @Shadow private CommandDispatcher<SharedSuggestionProvider> commands;
    @Shadow private ClientLevel level;
    @Shadow @Final private Minecraft minecraft;
    private static boolean mc_isInitialised = false;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V", shift = At.Shift.AFTER), method = "handlePlayerCombatKill")
    private void onDeathMessage(ClientboundPlayerCombatKillPacket packet, CallbackInfo cbi) {
        if (level.getEntity(packet.getPlayerId()) == minecraft.player) IDeathTracker.get().addDeath(level, Objects.requireNonNull(minecraft.player).position());
    }

    @Inject(at = @At("TAIL"), method = "handleCommands")
    public void onCommandTree(ClientboundCommandsPacket packet, CallbackInfo cbi) {
        if (!mc_isInitialised) {
            ClientCommandRegistrationEvent.EVENT.invoker().register(MoreCommandsClient.clientCommandDispatcher);
            mc_isInitialised = true;
        }
        for (CommandNode<?> node : MoreCommandsClient.clientCommandDispatcher.getRoot().getChildren())
            commands.getRoot().addChild((CommandNode<SharedSuggestionProvider>) node);
    }

    @Inject(at = @At("HEAD"), method = "handleSetTime", cancellable = true)
    public void onWorldTimeUpdate(ClientboundSetTimePacket packet, CallbackInfo cbi) {
        if (PtimeCommand.isEnabled()) {
            PtimeCommand.setServerTime(packet.getDayTime());
            cbi.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "handleGameEvent", cancellable = true)
    public void onGameStateChange(ClientboundGameEventPacket packet, CallbackInfo cbi) {
        if (PweatherCommand.pweather != PweatherCommand.WeatherType.OFF) {
            ClientboundGameEventPacket.Type reason = packet.getEvent();
            float f = packet.getParam();
            if (reason == ClientboundGameEventPacket.START_RAINING) {
                PweatherCommand.isRaining = true;
                PweatherCommand.rainGradient = 1f;
                cbi.cancel();
            } else if (reason == ClientboundGameEventPacket.STOP_RAINING) {
                PweatherCommand.isRaining = false;
                PweatherCommand.rainGradient = 0f;
                cbi.cancel();
            } else if (reason == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE) {
                PweatherCommand.rainGradient = f;
                cbi.cancel();
            } else if (reason == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
                PweatherCommand.thunderGradient = f;
                cbi.cancel();
            }
        }
    }
}
