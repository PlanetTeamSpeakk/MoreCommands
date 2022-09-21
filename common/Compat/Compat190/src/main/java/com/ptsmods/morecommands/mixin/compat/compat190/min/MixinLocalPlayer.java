package com.ptsmods.morecommands.mixin.compat.compat190.min;

import com.ptsmods.morecommands.api.IMoreCommandsClient;
import com.ptsmods.morecommands.api.callbacks.ChatMessageSendEvent;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    private @Unique boolean ignore = false;

    @Inject(at = @At("HEAD"), method = "chat(Ljava/lang/String;)V", cancellable = true)
    public void chat(String message, CallbackInfo cbi) {
        if (ignore) {
            ignore = false;
            return;
        }

        String oldMessage = message;
        message = ChatMessageSendEvent.EVENT.invoker().onMessageSend(message);
        if (message == null || message.isEmpty()) {
            cbi.cancel();
            return;
        }

        if (message.startsWith("/")) // For legacy reasons
            IMoreCommandsClient.handleCommand(message, cbi);

        if (!message.equals(oldMessage)) {
            cbi.cancel();

            ignore = true;
            chat(message);
        }
    }

    @Shadow
    public abstract void chat(String message);
}
