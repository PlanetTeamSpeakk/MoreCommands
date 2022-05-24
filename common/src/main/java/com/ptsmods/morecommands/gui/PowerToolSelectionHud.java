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
    public static final int DURATION = 1500, MAX_BOX_WIDTH = 160, FADE_OUT = 500;

    public static void render(MatrixStack matrices, float tickDelta) {
        if (currentSelection == null || System.currentTimeMillis() - currentSelection.getLeft() >= DURATION) return;
        Window window = MinecraftClient.getInstance().getWindow();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        String line1 = "Selected: " + currentSelection.getRight().getLeft();
        String line2 = textRenderer.getWidth(currentSelection.getRight().getRight()) > MAX_BOX_WIDTH ?
                trimToLength(currentSelection.getRight().getRight(), textRenderer) + "..." : currentSelection.getRight().getRight();

        int width = window.getScaledWidth(), height = window.getScaledHeight();
        int boxWidth = 2 + Math.max(textRenderer.getWidth(line1), textRenderer.getWidth(line2)) + 2;
        final int boxHeight = 2 + 8 + 2 + 8 + 2;

        float alphaMultiplier = Math.min((DURATION - (System.currentTimeMillis() - currentSelection.getLeft())) / (float) FADE_OUT, 1f);

        fill(matrices, width - boxWidth - 10, height / 2 - boxHeight / 2, width - 10, height / 2 + boxHeight / 2, new Color(0, 0, 0, .5f * alphaMultiplier).getRGB());

        textRenderer.drawWithShadow(matrices, LiteralTextBuilder.literal(line1), (float) (width - boxWidth / 2 - textRenderer.getWidth(line1) / 2 - 10), height / 2f - 9,
                withAlpha(Objects.requireNonNull(MoreCommands.DF.getColorValue()), alphaMultiplier));
        textRenderer.drawWithShadow(matrices, LiteralTextBuilder.literal(line2), (float) (width - boxWidth / 2 - textRenderer.getWidth(line2) / 2 - 10), height / 2f + 1,
                withAlpha(Objects.requireNonNull(MoreCommands.SF.getColorValue()), alphaMultiplier));
    }

    public static String trimToLength(String s, TextRenderer renderer) {
        StringBuilder result = new StringBuilder();

        for (char c : s.toCharArray()) {
            result.append(c);
            if (renderer.getWidth(result.toString()) > MAX_BOX_WIDTH) {
                result.deleteCharAt(result.length() - 1);
                break;
            }
        }

        return result.toString();
    }

    private static int withAlpha(int colour, float alpha) {
        Color c = new Color(colour);

        return new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f * alpha).getRGB();
    }
}
