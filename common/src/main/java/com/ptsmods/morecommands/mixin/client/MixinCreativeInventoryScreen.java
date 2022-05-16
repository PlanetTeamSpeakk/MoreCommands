package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.addons.CreativeInventoryScreenAddon;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeInventoryScreen.class, priority = 1100) // Higher priority so it gets injected after the Fabric API does.
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	@Shadow private TextFieldWidget searchBox;

	public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(IIIFFLnet/minecraft/entity/LivingEntity;)V"), method = "drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V")
	public void drawBackground_drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
		InventoryScreen.drawEntity(x, y, (int) (size * (ClientOptions.Rendering.renderOwnTag.getValue() ? 0.85f : 1f)), mouseX, mouseY, entity);
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;isPointWithinBounds(IIIIDD)Z"), method = "renderTabTooltipIfHovered", index = 1)
	private int renderTabTooltipIfHovered_isPointWithinBounds_yPosition(int y) {
		return y + 2;
	}

	@Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
	private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cbi) {
		if (!searchBox.isFocused() && ClientOptions.Tweaks.creativeKeyPager.getValue()) {
			CreativeInventoryScreenAddon addon = (CreativeInventoryScreenAddon) this;
			GameOptions options = MinecraftClient.getInstance().options;
			ButtonWidget btn = null;
			if ((options.leftKey.matchesKey(keyCode, scanCode) || options.backKey.matchesKey(keyCode, scanCode) ||
					keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_DOWN) && addon.mc$getPagerPrev() != null && addon.mc$getPagerPrev().active)
				btn = addon.mc$getPagerPrev();
			else if ((options.rightKey.matchesKey(keyCode, scanCode) || options.forwardKey.matchesKey(keyCode, scanCode) ||
					keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_UP) && addon.mc$getPagerNext() != null && addon.mc$getPagerNext().active)
				btn = addon.mc$getPagerNext();
			if (btn != null) {
				btn.onPress();
				btn.playDownSound(MinecraftClient.getInstance().getSoundManager());
				cbi.setReturnValue(true);
			}
		}
	}
}
