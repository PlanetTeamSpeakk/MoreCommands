package com.ptsmods.morecommands.api.miscellaneous;

import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.ChatFormatting;

@ExtensionMethod(ObjectExtensions.class)
public enum FormattingColour {
    BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, RAINBOW;

    @Getter(lazy = true)
    private static final ChatFormatting rainbow = getRainbowLazy();

    public ChatFormatting asFormatting() {
        return this == RAINBOW ? getRainbow().or(ChatFormatting.WHITE) : ChatFormatting.values()[ordinal()];
    }

    private static ChatFormatting getRainbowLazy() {
        try {
            return ChatFormatting.valueOf("RAINBOW");
        } catch (Exception e) {
            return null;
        }
    }
}
