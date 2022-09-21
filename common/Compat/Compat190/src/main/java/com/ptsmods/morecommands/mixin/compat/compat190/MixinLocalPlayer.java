package com.ptsmods.morecommands.mixin.compat.compat190;

import com.ptsmods.morecommands.api.IMoreCommandsClient;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSigner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    @Inject(at = @At("HEAD"), method = "sendCommand", require = 0, cancellable = true)
    public void sendCommand(MessageSigner signer, String command, Component text, CallbackInfo cbi) {
        IMoreCommandsClient.handleCommand(command, cbi);
    }
}
