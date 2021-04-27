package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.callbacks.RenderTickCallback;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Inject(at = @At("TAIL"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
	public void disconnect(Screen screen, CallbackInfo cbi) {
		// Reset to defaults when leaving the world.
		MoreCommands.setFormattings(Formatting.GOLD, Formatting.YELLOW);
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

}
