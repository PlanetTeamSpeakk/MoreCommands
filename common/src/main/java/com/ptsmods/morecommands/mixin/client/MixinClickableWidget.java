package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.addons.ScalableClickableWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClickableWidget.class)
public class MixinClickableWidget extends DrawableHelper implements ScalableClickableWidget {
    private @Unique boolean autoScale = false;
    private @Shadow int x, y, width, height;

    @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/client/gui/widget/ClickableWidget.drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"),
    method = "renderButton(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
    private void renderButton_drawCenteredText(MatrixStack stack, TextRenderer renderer, Text text, int x, int y, int colour) {
        if (!isAutoScale()) {
            drawCenteredText(stack, renderer, text, x, y, colour);
            return;
        }

        stack.push();
        float scale = Math.min((width - 12f) / renderer.getWidth(text), 1f);
        stack.scale(scale, scale, scale);
        drawCenteredText(stack, renderer, text, (int) ((this.x + width / 2) / scale), (int) ((this.y + (height - 8 * scale) / 2) / scale), colour);
        stack.pop();
    }

    @Override
    public void setAutoScale(boolean autoScale) {
        this.autoScale = autoScale;
    }

    @Override
    public boolean isAutoScale() {
        return autoScale;
    }
}
