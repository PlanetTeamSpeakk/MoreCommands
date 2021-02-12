package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.*;

// Also take a look at MixinTextVisitFactory and MixinTextColor
public class Rainbow {

    public static int rainbowIndex = 0;
    public static final List<Color> rainbowColours;
    public static final Formatting RAINBOW = ReflectionHelper.newEnumInstance(Formatting.class, new Class[] {String.class, char.class, boolean.class}, "RAINBOW", "RAINBOW", 'u', true);;
    public static final TextColor RAINBOW_TC = ReflectionHelper.newInstance(Objects.requireNonNull(ReflectionHelper.getConstructor(TextColor.class, int.class, String.class)), 0, "rainbow");

    static {
        List<Color> colours = new ArrayList<>();
        for (int r=0; r<=100; r++) colours.add(new Color(r*255/100,       255,         0));
        for (int g=100; g>=0; g--) colours.add(new Color(      255, g*255/100,         0));
        for (int b=0; b<=100; b++) colours.add(new Color(      255,         0, b*255/100));
        for (int r=100; r>=0; r--) colours.add(new Color(r*255/100,         0,       255));
        for (int g=0; g<=100; g++) colours.add(new Color(        0, g*255/100,       255));
        for (int b=100; b>=0; b--) colours.add(new Color(        0,       255, b*255/100));
        rainbowColours = ImmutableList.copyOf(colours); // Looks better than just going through hue with HSB imo tbh.
        // No so-called 'private' or 'final' field is safe from reflection. >:D
        // Not even if it's 'immutable'. smhh
        Field f = ReflectionHelper.getYarnField(TextColor.class, "FORMATTING_TO_COLOR", "field_24362");
        Map<Formatting, TextColor> formattingToColor = new HashMap<>(ReflectionHelper.getFieldValue(f, null));
        formattingToColor.put(RAINBOW, RAINBOW_TC);
        ReflectionHelper.setFieldValue(f, null, ImmutableMap.copyOf(formattingToColor));
        f = ReflectionHelper.getYarnField(TextColor.class, "BY_NAME", "field_24363");
        Map<String, TextColor> byName = new HashMap<>(ReflectionHelper.getFieldValue(f, null));
        byName.put("rainbow", RAINBOW_TC);
        ReflectionHelper.setFieldValue(f, null, ImmutableMap.copyOf(byName));
    }

    public static int getRainbowColour(boolean includeIndex) {
        return getRainbowColour(includeIndex, 1f);
    }

    public static int getRainbowColour(boolean includeIndex, float transparency) {
        Color c = rainbowColours.get((int) ((System.currentTimeMillis() + (includeIndex ? 20 * rainbowIndex : 0)) % rainbowColours.size()));
        if (transparency < 1f) c = new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, transparency);
        return c.getRGB(); /*Color.HSBtoRGB((System.currentTimeMillis() + 20 * rainbowIndex) % 720 / 720f, 1, 1)*/
    }

}
