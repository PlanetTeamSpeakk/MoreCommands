package com.ptsmods.morecommands.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.callbacks.ClientCommandRegistrationEvent;
import com.ptsmods.morecommands.api.callbacks.PlayerListEvent;
import com.ptsmods.morecommands.commands.client.PtimeCommand;
import com.ptsmods.morecommands.commands.client.PweatherCommand;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ClientPacketListener.class)
public class MixinClientPlayNetworkHandler {
    @Shadow private CommandDispatcher<SharedSuggestionProvider> commands;
    private static boolean mc_isInitialised = false;

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

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), method = "handlePlayerInfo")
    public Object onPlayerList_remove(Map<?, ?> map, Object key) {
        Object entry = map.remove(key);
        PlayerListEvent.REMOVE.invoker().call((PlayerInfo) entry);
        return entry;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", remap = false), method = "handlePlayerInfo")
    public Object onPlayerList_put(Map<Object, Object> map, Object key, Object value) {
        map.put(key, value);
        PlayerListEvent.ADD.invoker().call((PlayerInfo) value);
        return value;
    }
}
