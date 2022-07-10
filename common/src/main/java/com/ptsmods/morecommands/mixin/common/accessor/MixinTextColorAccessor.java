package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(TextColor.class)
public interface MixinTextColorAccessor {
    @Invoker("<init>")
    static TextColor newInstance(int value, String name) {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("LEGACY_FORMAT_TO_COLOR")
    static Map<ChatFormatting, TextColor> getLegacyFormatToColor() {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("LEGACY_FORMAT_TO_COLOR")
    @Mutable
    static void setLegacyFormatToColor(Map<ChatFormatting, TextColor> legacyFormatToColor) {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("NAMED_COLORS")
    static Map<String, TextColor> getNamedColors() {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("NAMED_COLORS")
    @Mutable
    static void setNamedColors(Map<String, TextColor> namedColors) {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("value")
    int getValue_();
}
