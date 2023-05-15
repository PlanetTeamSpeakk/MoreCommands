package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.util.Rainbow;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(StringDecomposer.class)
public class MixinStringDecomposer {

    /**
     * @author PlanetTeamSpeak
     * @reason Modifications were too hard to inject using conventional mixins.
     */
    @Overwrite
    public static boolean iterateFormatted(String text, int startIndex, Style startingStyle, Style rewithStyle, FormattedCharSink visitor) {
        int i = text.length();
        Style style = startingStyle;
        for (int j = startIndex; j < i; ++j) {
            if (Rainbow.getInstance() != null) Rainbow.getInstance().rainbowIndex = j; // Set rainbowIndex
            char c = text.charAt(j);
            char e;
            if (c == 167) {
                if (j + 1 >= i) {
                    break;
                }
                if (j + 7 < i && text.charAt(j + 1) == '#' && MoreCommands.isInteger(text.substring(j+2, j+8), 16)) { // Hex colours using formattings
                    style = style.withColor(TextColor.fromRgb(Integer.parseInt(text.substring(j+2, j+8), 16)));
                    j += 7;
                } else {
                    e = text.charAt(j + 1);
                    ChatFormatting formatting = ChatFormatting.getByCode(e);
                    if (formatting != null) {
                        style = formatting == ChatFormatting.RESET ? rewithStyle : style.applyLegacyFormat(formatting);
                    }
                    ++j;
                }
            } else if (Character.isHighSurrogate(c)) {
                if (j + 1 >= i) {
                    if (!visitor.accept(j, style, 65533)) {
                        return false;
                    }
                    break;
                }

                e = text.charAt(j + 1);
                if (Character.isLowSurrogate(e)) {
                    if (!visitor.accept(j, style, Character.toCodePoint(c, e))) {
                        return false;
                    }

                    ++j;
                } else if (!visitor.accept(j, style, 65533)) {
                    return false;
                }
            } else if (!feedChar(style, visitor, j, c)) {
                return false;
            }
        }

        return true;
    }

    @Shadow private static boolean feedChar(Style style, FormattedCharSink visitor, int index, char c) {
        return false;
    }

    @Inject(method = "iterate", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void storeIndex(String string, Style style, FormattedCharSink formattedCharSink, CallbackInfoReturnable<Boolean> cir, int i, int j) {
        if (Rainbow.getInstance() != null) Rainbow.getInstance().rainbowIndex = j;
    }
}
