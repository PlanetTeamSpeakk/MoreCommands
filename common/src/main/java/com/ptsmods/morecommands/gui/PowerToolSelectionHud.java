package com.ptsmods.morecommands.gui;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;

import java.awt.*;
import java.util.Objects;

public class PowerToolSelectionHud extends DrawableHelper {
    public static Pair<Long, Pair<Integer, String>> currentSelection = null;
    private static final int DURATION = 750, MAX_BOX_WIDTH = 160;

    public static void render(MatrixStack matrices, float tickDelta) {
        if (currentSelection == null || System.currentTimeMillis() - currentSelection.getLeft() >= DURATION) return;
        Window window = MinecraftClient.getInstance().getWindow();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        String line1 = "Selected: " + currentSelection.getRight().getLeft();
        String line2 = textRenderer.getWidth(currentSelection.getRight().getRight()) > MAX_BOX_WIDTH ?
                trimToLength(currentSelection.getRight().getRight(), textRenderer, MAX_BOX_WIDTH) + "..." : currentSelection.getRight().getRight();

        int width = window.getScaledWidth(), height = window.getScaledHeight();
        int boxWidth = 2 + Math.max(textRenderer.getWidth(line1), textRenderer.getWidth(line2)) + 2;
        final int boxHeight = 2 + 8 + 2 + 8 + 2;

        fill(matrices, width - boxWidth - 10, height / 2 - boxHeight / 2, width - 10, height / 2 + boxHeight / 2, new Color(0, 0, 0, .5f).getRGB());
        textRenderer.drawWithShadow(matrices, LiteralTextBuilder.literal(line1), (float) (width - boxWidth / 2 - textRenderer.getWidth(line1) / 2 - 10), height / 2f - 9, Objects.requireNonNull(MoreCommands.DF.getColorValue()));
        textRenderer.drawWithShadow(matrices, LiteralTextBuilder.literal(line2), (float) (width - boxWidth / 2 - textRenderer.getWidth(line2) / 2 - 10), height / 2f + 1, Objects.requireNonNull(MoreCommands.SF.getColorValue()));
    }

    @SuppressWarnings("SameParameterValue") // Don't care
    private static String trimToLength(String s, TextRenderer renderer, int pixelLength) {
        StringBuilder result = new StringBuilder();

        for (char c : s.toCharArray()) {
            result.append(c);
            if (renderer.getWidth(result.toString()) > pixelLength) {
                result.deleteCharAt(result.length() - 1);
                break;
            }
        }

        return result.toString();
    }
}
