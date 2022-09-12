package com.ptsmods.morecommands.mixin.compat.compat191.plus;

import com.ptsmods.morecommands.api.IMoreCommandsClient;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {

    @Inject(at = @At("HEAD"), method = "commandUnsigned", cancellable = true)
    public void commandUnsigned(String message, CallbackInfoReturnable<Boolean> cbi) {
        IMoreCommandsClient.handleCommand(message, cbi);
    }

    @Inject(at = @At("HEAD"), method = "sendCommand", cancellable = true)
    public void sendCommand(String message, Component component, CallbackInfo cbi) {
        IMoreCommandsClient.handleCommand(message, cbi);
    }
}
