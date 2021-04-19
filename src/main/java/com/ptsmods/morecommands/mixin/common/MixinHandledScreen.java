package com.ptsmods.morecommands.mixin.common;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer; renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"), method = "drawSlot(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/screen/slot/Slot;)V")
	private void drawSlot_renderGuiItemOverlay(ItemRenderer itemRenderer, TextRenderer textRenderer, ItemStack stack, int x, int y, String countLabel, MatrixStack matrices, Slot slot) {
		itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y, stack.hasTag() && stack.getTag().contains("Unlimited") ? "111" : countLabel);
	}

}
