package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.regex.Pattern;

@Mixin(Formatting.class)
public interface MixinFormattingAccessor {
    @Accessor("FORMATTING_CODE_PATTERN")
    @Mutable
    static void setFormattingCodePattern(Pattern pattern) {
        throw new AssertionError("This shouldn't happen.");
    }
}
