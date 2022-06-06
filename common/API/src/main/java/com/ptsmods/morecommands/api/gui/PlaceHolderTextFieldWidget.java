package com.ptsmods.morecommands.api.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class PlaceHolderTextFieldWidget extends TextFieldWidget {
    private final TextRenderer textRenderer;
    private Text placeholder;
    private boolean drawsBackground = true;

    public PlaceHolderTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder) {
        this(textRenderer, x, y, width, height, null, placeholder);
    }

    public PlaceHolderTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text placeholder) {
        super(textRenderer, x, y, width, height, copyFrom, placeholder);
        this.textRenderer = textRenderer;
        this.placeholder = placeholder;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);

        if (!getText().isEmpty()) return;
        int x = drawsBackground ? this.x + 4 : this.x;
        int y = drawsBackground ? this.y + (this.height - 8) / 2 : this.y;

        textRenderer.drawWithShadow(matrices, placeholder, x, y, 11184810);
    }

    @Override
    public void setDrawsBackground(boolean drawsBackground) {
        super.setDrawsBackground(drawsBackground);
        this.drawsBackground = drawsBackground;
    }

    @Override
    public void setRenderTextProvider(BiFunction<String, Integer, OrderedText> renderTextProvider) {
        super.setRenderTextProvider(renderTextProvider);
    }
}
