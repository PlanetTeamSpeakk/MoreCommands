package com.ptsmods.morecommands.api.util;

import net.minecraft.util.Formatting;

public class Util {

	public static Formatting formatFromBool(boolean b) {
		return b ? Formatting.GREEN : Formatting.RED;
	}

	public static String formatFromBool(boolean b, String yes, String no) {
		return formatFromBool(b) + (b ? yes : no);
	}

	public static String translateFormats(String s) {
		for (Formatting f : Formatting.values())
			s = s.replaceAll("&" + f.toString().charAt(1), f.toString());
		return s.replaceAll("&#", "\u00A7#");
	}
}
