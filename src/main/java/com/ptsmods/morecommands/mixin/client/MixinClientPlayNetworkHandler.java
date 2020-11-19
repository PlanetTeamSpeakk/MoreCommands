package com.ptsmods.morecommands.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.commands.client.PtimeCommand;
import com.ptsmods.morecommands.callbacks.ClientCommandRegistrationCallback;
import com.ptsmods.morecommands.commands.client.PweatherCommand;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;
    private boolean mc_isInitialised = false;

    @Inject(at = @At("TAIL"), method = "onCommandTree(Lnet/minecraft/network/packet/s2c/play/CommandTreeS2CPacket;)V")
    public void onCommandTree(CommandTreeS2CPacket packet, CallbackInfo cbi) {
        if (!mc_isInitialised) {
            ClientCommandRegistrationCallback.EVENT.invoker().register(MoreCommandsClient.clientCommandDispatcher);
            mc_isInitialised = true;
        }
        for (CommandNode<?> node : MoreCommandsClient.clientCommandDispatcher.getRoot().getChildren())
            commandDispatcher.getRoot().addChild((CommandNode<CommandSource>) node);
    }

    @Inject(at = @At("HEAD"), method = "onWorldTimeUpdate(Lnet/minecraft/network/packet/s2c/play/WorldTimeUpdateS2CPacket;)V", cancellable = true)
    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo cbi) {
        if (PtimeCommand.isEnabled()) {
            PtimeCommand.setServerTime(packet.getTimeOfDay());
            cbi.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onGameStateChange(Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket;)V", cancellable = true)
    public void onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo cbi) {
        if (PweatherCommand.pweather != PweatherCommand.WeatherType.OFF) {
            GameStateChangeS2CPacket.Reason reason = packet.getReason();
            float f = packet.getValue();
            if (reason == GameStateChangeS2CPacket.RAIN_STARTED) {
                PweatherCommand.isRaining = true;
                PweatherCommand.rainGradient = 1f;
                cbi.cancel();
            } else if (reason == GameStateChangeS2CPacket.RAIN_STOPPED) {
                PweatherCommand.isRaining = false;
                PweatherCommand.rainGradient = 0f;
                cbi.cancel();
            } else if (reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
                PweatherCommand.rainGradient = f;
                cbi.cancel();
            } else if (reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
                PweatherCommand.thunderGradient = f;
                cbi.cancel();
            }
        }
    }

}
