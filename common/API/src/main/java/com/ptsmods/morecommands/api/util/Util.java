package com.ptsmods.morecommands.api.util;

import lombok.experimental.UtilityClass;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@UtilityClass
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

    public static boolean stackEquals(ItemStack stack1, ItemStack stack2) {
        return stack1 != null && stack2 != null &&
                (stack1 == stack2 ||
                stack1.getItem() == stack2.getItem() &&
                stack1.getCount() == stack2.getCount() &&
                Objects.equals(stack1.getTag(), stack2.getTag()));
    }
}
