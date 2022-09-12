package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.TreeMap;

public class BetterLanguage extends Language {
    private static final TreeMap<Integer, String> romanNumerals = new TreeMap<>();
    private final Language parent;

    static {
        romanNumerals.put(100, "C");
        romanNumerals.put(90, "XC");
        romanNumerals.put(50, "L");
        romanNumerals.put(40, "XL");
        romanNumerals.put(10, "X");
        romanNumerals.put(9, "IX");
        romanNumerals.put(5, "V");
        romanNumerals.put(4, "IV");
        romanNumerals.put(1, "I");
    }

    public BetterLanguage(Language parent) {
        this.parent = Objects.requireNonNull(parent);
    }

    @Override
    public String getOrDefault(@NotNull String key) {
        return "null".equals(key) ?
                null : key.startsWith("block.minecraft.spawner_") && key.indexOf('_') != key.length()-1 ?
                getOrDefault(key.split("_", 2)[1]) + " " + getOrDefault("block.minecraft.spawner") : key.startsWith("enchantment.level.") ?
                toRoman(Integer.parseInt(key.split("\\.")[2])) : parent.getOrDefault(key);
    }

    @Override
    public boolean has(@NotNull String key) {
        return key.startsWith("enchantment.level.") || parent.has(key);
    }

    @Override
    public boolean isDefaultRightToLeft() {
        return parent.isDefaultRightToLeft();
    }

    @Override
    public FormattedCharSequence getVisualOrder(@NotNull FormattedText text) {
        return parent.getVisualOrder(text);
    }

    private String toRoman(int number) {
        if (number > 100 || number < 1) return String.valueOf(number);
        int l = romanNumerals.floorKey(number);
        if (number == l) return romanNumerals.get(number);
        return romanNumerals.get(l) + toRoman(number-l);
    }
}
