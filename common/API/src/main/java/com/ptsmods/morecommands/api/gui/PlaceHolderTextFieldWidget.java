package com.ptsmods.morecommands.api.gui;

import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiFunction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class PlaceHolderTextFieldWidget extends EditBox {
    private final Font textRenderer;
    private Component placeholder;
    private boolean drawsBackground = true;

    public PlaceHolderTextFieldWidget(Font textRenderer, int x, int y, int width, int height, Component placeholder) {
        this(textRenderer, x, y, width, height, null, placeholder);
    }

    public PlaceHolderTextFieldWidget(Font textRenderer, int x, int y, int width, int height, @Nullable EditBox copyFrom, Component placeholder) {
        super(textRenderer, x, y, width, height, copyFrom, placeholder);
        this.textRenderer = textRenderer;
        this.placeholder = placeholder;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);

        if (!getValue().isEmpty()) return;
        int x = drawsBackground ? this.x + 4 : this.x;
        int y = drawsBackground ? this.y + (this.height - 8) / 2 : this.y;

        textRenderer.drawShadow(matrices, placeholder, x, y, 11184810);
    }

    @Override
    public void setBordered(boolean drawsBackground) {
        super.setBordered(drawsBackground);
        this.drawsBackground = drawsBackground;
    }

    @Override
    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> renderTextProvider) {
        super.setFormatter(renderTextProvider);
    }
}
