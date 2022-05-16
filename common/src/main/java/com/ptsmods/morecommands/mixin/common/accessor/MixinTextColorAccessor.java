package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(TextColor.class)
public interface MixinTextColorAccessor {
	@Invoker("<init>")
	static TextColor newInstance(int rgb, String name) {
		throw new AssertionError("This shouldn't happen.");
	}

	@Accessor("FORMATTING_TO_COLOR")
	static Map<Formatting, TextColor> getFormattingToColor() {
		throw new AssertionError("This shouldn't happen.");
	}

	@Accessor("FORMATTING_TO_COLOR")
	@Mutable
	static void setFormattingToColor(Map<Formatting, TextColor> formattingToColor) {
		throw new AssertionError("This shouldn't happen.");
	}

	@Accessor("BY_NAME")
	static Map<String, TextColor> getByName() {
		throw new AssertionError("This shouldn't happen.");
	}

	@Accessor("BY_NAME")
	@Mutable
	static void setByName(Map<String, TextColor> byName) {
		throw new AssertionError("This shouldn't happen.");
	}

	@Accessor("rgb")
	int getRgb_();
}
