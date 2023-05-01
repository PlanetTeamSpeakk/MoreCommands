package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Inject(at = @At("HEAD"), method = "renderGuiItemDecorations(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", cancellable = true)
    public void renderGuiItemOverlay(PoseStack poseStack, Font font, ItemStack stack, int x, int y, String countLabel, CallbackInfo cbi) {
        if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("Unlimited") && (countLabel == null || !countLabel.endsWith("111"))) {
            if (countLabel != null) countLabel = countLabel.replace(String.valueOf(stack.getCount()), "111");
            else countLabel = "111";
            cbi.cancel();
            ReflectionHelper.<ItemRenderer>cast(this).renderGuiItemDecorations(poseStack, font, stack, x, y, countLabel);
        }
    }
}
