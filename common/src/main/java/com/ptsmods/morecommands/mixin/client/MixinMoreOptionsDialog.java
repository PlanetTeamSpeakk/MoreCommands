package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.gui.WorldInitCommandsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoreOptionsDialog.class)
public abstract class MixinMoreOptionsDialog {
	@Unique private ButtonWidget wicBtn;

	@Inject(at = @At("TAIL"), method = "init")
	private void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer, CallbackInfo ci) {
		((ScreenAddon) parent).mc$addButton(wicBtn = new ButtonWidget(parent.width / 2 + 5, 151, 150, 20, LiteralTextBuilder.builder("Initialisation Commands").build(),
				btn -> MinecraftClient.getInstance().setScreen(new WorldInitCommandsScreen(parent)))).visible = false;
	}

	@Inject(at = @At("TAIL"), method = "setVisible")
	public void setVisible(boolean visible, CallbackInfo ci) {
		wicBtn.visible = isDebugWorld() && visible;
	}

	@Shadow public abstract boolean isDebugWorld();
}
