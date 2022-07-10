package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.regex.Pattern;

@Mixin(ChatFormatting.class)
public interface MixinFormattingAccessor {
    @Accessor("STRIP_FORMATTING_PATTERN")
    @Mutable
    static void setStripFormattingPattern(Pattern pattern) {
        throw new AssertionError("This shouldn't happen.");
    }
}
