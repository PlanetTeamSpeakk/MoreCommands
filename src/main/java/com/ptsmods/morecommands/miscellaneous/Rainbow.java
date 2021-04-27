package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.mixin.common.accessor.MixinTextColorAccessor;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;

import java.awt.Color;
import java.util.*;

// Also take a look at MixinTextVisitFactory and MixinTextColor
public class Rainbow {
	private static boolean checked = false;
	private static Rainbow instance = null;

	public static Rainbow getInstance() {
		if (checked) return instance;
		checked = true;
		try {
			Formatting.valueOf("RAINBOW");
		} catch (IllegalArgumentException e) {
			LogManager.getLogger("MoreCommands-Rainbow").info("The RAINBOW formatting could not be found which means the Formatting class was initialised too early, no rainbows will be supported during this session. Are you using MultiMC perhaps?");
			return null;
		}
		return instance == null ? instance = new Rainbow() : instance;
	}

	public int rainbowIndex = 0;
	public final List<Color> rainbowColours;
	public final Formatting RAINBOW = Formatting.valueOf("RAINBOW"); // Should've been registered in EarlyRiser.
	public final TextColor RAINBOW_TC = MixinTextColorAccessor.newInstance(0, "rainbow");

	private Rainbow() {
		List<Color> colours = new ArrayList<>();
		for (int r=0; r<=100; r++) colours.add(new Color(r*255/100,	   255,		 0));
		for (int g=100; g>=0; g--) colours.add(new Color(	  255, g*255/100,		 0));
		for (int b=0; b<=100; b++) colours.add(new Color(	  255,		 0, b*255/100));
		for (int r=100; r>=0; r--) colours.add(new Color(r*255/100,		 0,	   255));
		for (int g=0; g<=100; g++) colours.add(new Color(		0, g*255/100,	   255));
		for (int b=100; b>=0; b--) colours.add(new Color(		0,	   255, b*255/100));
		rainbowColours = ImmutableList.copyOf(colours); // Looks better than just going through hue with HSB imo tbh.
		// No so-called 'private' or 'final' field is safe from reflection. >:D
		// Not even if it's 'immutable'. smhh
		Map<Formatting, TextColor> formattingToColor = new HashMap<>(MixinTextColorAccessor.getFormattingToColor());//new HashMap<>(ReflectionHelper.getFieldValue(f, null));
		formattingToColor.put(RAINBOW, RAINBOW_TC);
		MixinTextColorAccessor.setFormattingToColor(formattingToColor);

		Map<String, TextColor> byName = new HashMap<>(MixinTextColorAccessor.getByName());
		byName.put("rainbow", RAINBOW_TC);
		MixinTextColorAccessor.setByName(byName);
	}

	public int getRainbowColour(boolean includeIndex) {
		return getRainbowColour(includeIndex, 1f);
	}

	public int getRainbowColour(boolean includeIndex, float transparency) {
		Color c = rainbowColours.get((int) ((System.currentTimeMillis() + (includeIndex ? 20 * rainbowIndex : 0)) % rainbowColours.size()));
		if (transparency < 1f) c = new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, transparency);
		return c.getRGB(); /*Color.HSBtoRGB((System.currentTimeMillis() + 20 * rainbowIndex) % 720 / 720f, 1, 1)*/
	}

}
