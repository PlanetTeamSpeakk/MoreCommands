package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import net.minecraft.text.KeybindTranslations;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(KeybindTranslations.class)
public interface MixinKeybindTranslationsAccessor {

	@Accessor("FACTORY")
	static Function<String, Supplier<Text>> getFactory() {
		throw new AssertionError("This shouldn't happen.");
	}
}
