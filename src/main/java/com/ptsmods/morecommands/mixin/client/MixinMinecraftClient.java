package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.callbacks.PostInitCallback;
import com.ptsmods.morecommands.callbacks.RenderTickCallback;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
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
		ClientOptions.getOptions().forEach(option -> option.setDisabled(false));
		MoreCommandsClient.clearDisabledCommands();
	}

	@Inject(at = @At("HEAD"), method = "render(Z)V")
	public void renderPre(boolean tick, CallbackInfo cbi) {
		RenderTickCallback.PRE.invoker().render(tick);
	}

	@Inject(at = @At("TAIL"), method = "render(Z)V")
	public void renderPost(boolean tick, CallbackInfo cbi) {
		RenderTickCallback.POST.invoker().render(tick);
	}

	@Inject(at = @At("TAIL"), method = "setCurrentServerEntry(Lnet/minecraft/client/network/ServerInfo;)V")
	public void setCurrentServerEntry(ServerInfo info, CallbackInfo cbi) {
		if (info != null) MoreCommandsClient.updatePresence();
	}

	@Inject(at = @At("TAIL"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
	public void joinWorld(ClientWorld world, CallbackInfo cbi) {
		MoreCommandsClient.updatePresence();
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/entrypoint/minecraft/hooks/EntrypointClient;start(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "<init>")
	private void postInit(RunArgs args, CallbackInfo ci) {
		PostInitCallback.EVENT.invoker().postInit();
	}

	@Inject(at = @At("HEAD"), method = "createWorld")
	private void createWorldPre(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
		createdWorld = true;
	}

	@Inject(at = @At("TAIL"), method = "createWorld")
	private void createWorldPost(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
		createdWorld = false;
	}

	@ModifyVariable(at = @At("STORE"), method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V")
	private ClientConnection startIntegratedServer_integratedServerConnection(ClientConnection connection) {
		if (createdWorld) MoreCommandsClient.scheduleWorldInitCommands = true;
		return connection;
	}
}
