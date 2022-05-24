package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.callbacks.PostInitEvent;
import com.ptsmods.morecommands.api.callbacks.RenderTickEvent;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 1100)
public class MixinMinecraftClient {
    @Unique
    private boolean createdWorld = false;

    @Inject(at = @At("TAIL"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    public void disconnect(Screen screen, CallbackInfo cbi) {
        // Reset to defaults when leaving the world.
        MoreCommands.setFormattings(ClientOptions.Tweaks.defColour.getValue().asFormatting(), ClientOptions.Tweaks.secColour.getValue().asFormatting());
        SearchCommand.lines.clear();
        MoreCommandsClient.updatePresence();
        ClientOption.getUnmappedOptions().values().forEach(option -> option.setDisabled(false));
        MoreCommandsClient.clearDisabledCommands();
    }

    @Inject(at = @At("HEAD"), method = "render(Z)V")
    public void renderPre(boolean tick, CallbackInfo cbi) {
        RenderTickEvent.PRE.invoker().render(tick);
    }

    @Inject(at = @At("TAIL"), method = "render(Z)V")
    public void renderPost(boolean tick, CallbackInfo cbi) {
        RenderTickEvent.POST.invoker().render(tick);
    }

    @Inject(at = @At("TAIL"), method = "setCurrentServerEntry(Lnet/minecraft/client/network/ServerInfo;)V")
    public void setCurrentServerEntry(ServerInfo info, CallbackInfo cbi) {
        if (info != null) MoreCommandsClient.updatePresence();
    }

    @Inject(at = @At("TAIL"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
    public void joinWorld(ClientWorld world, CallbackInfo cbi) {
        MoreCommandsClient.updatePresence();
    }

    @Group(name = "postInitClient", min = 1, max = 1)
    @Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/entrypoint/minecraft/hooks/EntrypointClient;start(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "<init>")
    private void postInitOld(RunArgs args, CallbackInfo cbi) {
        PostInitEvent.EVENT.invoker().postInit();
    }

    @Group(name = "postInitClient", min = 1, max = 1)
    @Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startClient(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "<init>")
    private void postInitNew(RunArgs args, CallbackInfo cbi) {
        PostInitEvent.EVENT.invoker().postInit();
    }

    @ModifyVariable(at = @At("STORE"), method = {"startIntegratedServer", "method_29610", "doWorldLoad"}, require = 1, remap = false)
    private ClientConnection startIntegratedServer_integratedServerConnectionNew(ClientConnection connection) {
        if (MoreCommands.creatingWorld) MoreCommandsClient.scheduleWorldInitCommands = true;
        MoreCommands.creatingWorld = false;
        return connection;
    }
}
