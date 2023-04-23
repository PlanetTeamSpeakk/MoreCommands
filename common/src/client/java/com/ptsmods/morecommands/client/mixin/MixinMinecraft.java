package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.api.MessageHistory;
import com.ptsmods.morecommands.api.callbacks.RenderTickEvent;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(at = @At("TAIL"), method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V")
    public void disconnect(Screen screen, CallbackInfo cbi) {
        // Reset to defaults when leaving the world.
        MoreCommands.setFormattings(ClientOptions.Tweaks.defColour.getValue().asFormatting(), ClientOptions.Tweaks.secColour.getValue().asFormatting());
        MessageHistory.clear();
        ClientOption.getUnmappedOptions().values().forEach(option -> option.setDisabled(false));
        MoreCommandsClient.clearDisabledCommands();
        IDeathTracker.get().reset();
    }

    @Inject(at = @At("HEAD"), method = "runTick")
    public void renderPre(boolean tick, CallbackInfo cbi) {
        RenderTickEvent.PRE.invoker().render(tick);
    }

    @Inject(at = @At("TAIL"), method = "runTick")
    public void renderPost(boolean tick, CallbackInfo cbi) {
        RenderTickEvent.POST.invoker().render(tick);
    }
}
