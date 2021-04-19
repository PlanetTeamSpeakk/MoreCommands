package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.util.Formatting;

public enum FormattingColour {
	BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, RAINBOW;

	public Formatting toFormatting() {
		return this == RAINBOW && Rainbow.getInstance() != null ? Rainbow.getInstance().RAINBOW : Formatting.values()[ordinal()];
	}
}
