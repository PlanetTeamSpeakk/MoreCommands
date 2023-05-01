package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ScalableWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractButton.class)
public abstract class MixinAbstractButton extends AbstractWidget {

    public MixinAbstractButton(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractButton;renderString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;I)V"),
            method = "renderWidget")
    private void renderButton_drawCenteredText(AbstractButton instance, PoseStack stack, Font font, int i) {
        AbstractButton thiz = ReflectionHelper.cast(this);
        ScalableWidget scalable = (ScalableWidget) this;
        if (!scalable.isAutoScale()) {
            thiz.renderString(stack, font, i);
            return;
        }

        stack.pushPose();
        float scale = Math.min((width - 12f) / font.width(getMessage()), 1f);
        stack.scale(scale, scale, scale);
        thiz.renderString(stack, font, i);
        // TODO
//        drawCenteredString(stack, renderer, text, (int) ((this.x + width / 2) / scale), (int) ((this.y + (height - 8 * scale) / 2) / scale), colour);
        stack.popPose();
    }
}
