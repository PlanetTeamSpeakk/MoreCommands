package com.ptsmods.morecommands.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Tuple;

import java.awt.*;
import java.util.Objects;

public class PowerToolSelectionHud extends GuiComponent {
    public static Tuple<Long, Tuple<Integer, String>> currentSelection = null;
    public static final int DURATION = 1500, MAX_BOX_WIDTH = 160, FADE_OUT = 500;

    public static void render(PoseStack matrices, float tickDelta) {
        if (currentSelection == null || System.currentTimeMillis() - currentSelection.getA() >= DURATION) return;
        Window window = Minecraft.getInstance().getWindow();
        Font textRenderer = Minecraft.getInstance().font;

        String line1 = "Selected: " + currentSelection.getB().getA();
        String line2 = textRenderer.width(currentSelection.getB().getB()) > MAX_BOX_WIDTH ?
                trimToLength(currentSelection.getB().getB(), textRenderer) + "..." : currentSelection.getB().getB();

        int width = window.getGuiScaledWidth(), height = window.getGuiScaledHeight();
        int boxWidth = 2 + Math.max(textRenderer.width(line1), textRenderer.width(line2)) + 2;
        final int boxHeight = 2 + 8 + 2 + 8 + 2;

        float alphaMultiplier = Math.min((DURATION - (System.currentTimeMillis() - currentSelection.getA())) / (float) FADE_OUT, 1f);

        fill(matrices, width - boxWidth - 10, height / 2 - boxHeight / 2, width - 10, height / 2 + boxHeight / 2, new Color(0, 0, 0, .5f * alphaMultiplier).getRGB());

        textRenderer.drawShadow(matrices, LiteralTextBuilder.literal(line1), (float) (width - boxWidth / 2 - textRenderer.width(line1) / 2 - 10), height / 2f - 9,
                withAlpha(Objects.requireNonNull(MoreCommands.DF.getColor()), alphaMultiplier));
        textRenderer.drawShadow(matrices, LiteralTextBuilder.literal(line2), (float) (width - boxWidth / 2 - textRenderer.width(line2) / 2 - 10), height / 2f + 1,
                withAlpha(Objects.requireNonNull(MoreCommands.SF.getColor()), alphaMultiplier));
    }

    public static String trimToLength(String s, Font renderer) {
        StringBuilder result = new StringBuilder();

        for (char c : s.toCharArray()) {
            result.append(c);
            if (renderer.width(result.toString()) > MAX_BOX_WIDTH) {
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
