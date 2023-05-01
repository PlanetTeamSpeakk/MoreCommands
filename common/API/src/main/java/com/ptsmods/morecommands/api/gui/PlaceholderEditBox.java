package com.ptsmods.morecommands.api.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class PlaceholderEditBox extends EditBox {
    private final Font textRenderer;
    private final Component placeholder;
    private boolean drawsBackground = true;
    private int x, y;

    public PlaceholderEditBox(Font textRenderer, int x, int y, int width, int height, Component placeholder) {
        this(textRenderer, x, y, width, height, null, placeholder);
    }

    public PlaceholderEditBox(Font textRenderer, int x, int y, int width, int height, @Nullable EditBox copyFrom, Component placeholder) {
        super(textRenderer, x, y, width, height, copyFrom, placeholder);
        this.textRenderer = textRenderer;
        this.placeholder = placeholder;

        this.x = x;
        this.y = y;
    }

    public void renderPlaceholder(PoseStack poseStack) {
        if (!getValue().isEmpty()) return;
        int x = drawsBackground ? this.x + 4 : this.x;
        int y = drawsBackground ? this.y + (this.height - 8) / 2 : this.y;

        textRenderer.drawShadow(poseStack, placeholder, x, y, 11184810);
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

    @Override
    public void setX(int x) {
        super.setX(x);
        this.x = x;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.y = y;
    }
}
