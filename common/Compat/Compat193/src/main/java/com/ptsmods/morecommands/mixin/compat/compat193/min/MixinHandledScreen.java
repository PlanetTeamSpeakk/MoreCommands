package com.ptsmods.morecommands.mixin.compat.compat193.min;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(AbstractContainerScreen.class)
public class MixinHandledScreen {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"), method = "renderSlot")
    private void drawSlot_renderGuiItemOverlay(ItemRenderer itemRenderer, Font textRenderer, ItemStack stack, int x, int y, String countLabel, PoseStack matrices, Slot slot) {
        itemRenderer.renderGuiItemDecorations(textRenderer, stack, x, y, stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("Unlimited") ? "111" : countLabel);
    }
}
