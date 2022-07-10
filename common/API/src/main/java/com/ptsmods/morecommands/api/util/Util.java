package com.ptsmods.morecommands.api.util;

import net.minecraft.ChatFormatting;

public class Util {

    public static ChatFormatting formatFromBool(boolean b) {
        return b ? ChatFormatting.GREEN : ChatFormatting.RED;
    }

    public static String formatFromBool(boolean b, String yes, String no) {
        return formatFromBool(b) + (b ? yes : no);
    }

    public static String translateFormats(String s) {
        for (ChatFormatting f : ChatFormatting.values())
            s = s.replaceAll("&" + f.toString().charAt(1), f.toString());
        return s.replaceAll("&#", "\u00A7#");
    }
}
