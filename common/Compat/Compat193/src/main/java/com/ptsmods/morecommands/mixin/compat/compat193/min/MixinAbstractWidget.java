package com.ptsmods.morecommands.mixin.compat.compat193.min;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.addons.ScalableWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractWidget.class)
public class MixinAbstractWidget extends GuiComponent {
    public @Shadow int x, y, width, height;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"),
            method = "renderButton")
    private void renderButton_drawCenteredText(PoseStack stack, Font renderer, Component text, int x, int y, int colour) {
        ScalableWidget scalable = (ScalableWidget) this;
        if (!scalable.isAutoScale()) {
            drawCenteredString(stack, renderer, text, x, y, colour);
            return;
        }

        stack.pushPose();
        float scale = Math.min((width - 12f) / renderer.width(text), 1f);
        stack.scale(scale, scale, scale);
        drawCenteredString(stack, renderer, text, (int) ((this.x + width / 2) / scale), (int) ((this.y + (height - 8 * scale) / 2) / scale), colour);
        stack.popPose();
    }
}
