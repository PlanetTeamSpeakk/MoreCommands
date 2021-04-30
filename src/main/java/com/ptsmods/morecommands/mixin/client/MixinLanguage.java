package com.ptsmods.morecommands.mixin.client;

import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.TreeMap;

@Mixin(Language.class)
public class MixinLanguage {

	@Shadow
	private static volatile Language instance;
	private static final TreeMap<Integer, String> romanNumerals = new TreeMap<>();

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

	/**
	 * @author PlanetTeamSpeak
	 */
	@Overwrite
	public static void setInstance(Language language) {
		instance = new Language() {

			@Override
			public String get(String key) {
				return String.valueOf(key).equals("null") ? null : key.startsWith("block.minecraft.spawner_") && key.indexOf('_') != key.length()-1 ? get(key.split("_", 2)[1]) + " " + get("block.minecraft.spawner") : key.startsWith("enchantment.level.") ? toRoman(Integer.parseInt(key.split("\\.")[2])) : language.get(key);
			}

			@Override
			public boolean hasTranslation(String key) {
				return key != null && (key.startsWith("enchantment.level.") || language.hasTranslation(key));
			}

			@Override
			public boolean isRightToLeft() {
				return language.isRightToLeft();
			}

			@Override
			public OrderedText reorder(StringVisitable text) {
				return language.reorder(text);
			}

			private String toRoman(int number) {
				if (number > 100 || number < 0) return String.valueOf(number);
				int l = romanNumerals.floorKey(number);
				if (number == l) return romanNumerals.get(number);
				return romanNumerals.get(l) + toRoman(number-l);
			}
		};
	}

}
