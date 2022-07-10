package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.api.callbacks.RenderTickEvent;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Inject(at = @At("TAIL"), method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V")
    public void disconnect(Screen screen, CallbackInfo cbi) {
        // Reset to defaults when leaving the world.
        MoreCommands.setFormattings(ClientOptions.Tweaks.defColour.getValue().asFormatting(), ClientOptions.Tweaks.secColour.getValue().asFormatting());
        SearchCommand.lines.clear();
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

    @ModifyVariable(at = @At("STORE"), method = "doWorldLoad", require = 1)
    private Connection startIntegratedServer_integratedServerConnectionNew(Connection connection) {
        if (MoreCommands.creatingWorld) MoreCommandsClient.scheduleWorldInitCommands = true;
        MoreCommands.creatingWorld = false;
        return connection;
    }
}
