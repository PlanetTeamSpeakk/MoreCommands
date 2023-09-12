package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.KeybindResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(KeybindResolver.class)
public interface MixinKeybindResolverAccessor {

    @Accessor
    static Function<String, Supplier<Component>> getKeyResolver() {
        throw new AssertionError("This shouldn't happen.");
    }
}
