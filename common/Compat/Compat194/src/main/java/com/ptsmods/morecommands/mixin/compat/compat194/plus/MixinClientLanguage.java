package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.TreeMap;

@Mixin(ClientLanguage.class)
public abstract class MixinClientLanguage {
    private static final @Unique TreeMap<Integer, String> romanNumerals = new TreeMap<>();

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

    @Shadow public abstract String getOrDefault(String key, String fallback);

    private @Unique String getOrDefault(String key) {
        return getOrDefault(key, key);
    }

    @Inject(method = "getOrDefault", at = @At("HEAD"), cancellable = true)
    private void provideExtendedTranslations(String key, String fallback, CallbackInfoReturnable<String> cir) {
        if (key == null) return;
        if (key.startsWith("block.minecraft.spawner_") && key.indexOf('_') != key.length() - 1)
            cir.setReturnValue(getOrDefault(key.split("_", 2)[1]) + " " + getOrDefault("block.minecraft.spawner"));
        if (key.startsWith("enchantment.level.")) cir.setReturnValue(toRoman(Integer.parseInt(key.split("\\.")[2])));
    }

    private @Unique String toRoman(int number) {
        if (number > 100 || number < 1) return String.valueOf(number);
        int l = romanNumerals.floorKey(number);
        if (number == l) return romanNumerals.get(number);
        return romanNumerals.get(l) + toRoman(number-l);
    }
}
