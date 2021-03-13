package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.Rainbow;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextVisitFactory.class)
public class MixinTextVisitFactory {

    /**
     * @author PlanetTeamSpeak
     */
    @Overwrite
    public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
        int i = text.length();
        Style style = startingStyle;
        for (int j = startIndex; j < i; ++j) {
            Rainbow.rainbowIndex = j; // Set rainbowIndex
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
                    Formatting formatting = Formatting.byCode(e);
                    if (formatting != null) {
                        style = formatting == Formatting.RESET ? resetStyle : style.withExclusiveFormatting(formatting);
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
            } else if (!visitRegularCharacter(style, visitor, j, c)) {
                return false;
            }
        }

        return true;
    }

    @Shadow
    private static boolean visitRegularCharacter(Style style, CharacterVisitor visitor, int index, char c) {
        return false;
    }

}
