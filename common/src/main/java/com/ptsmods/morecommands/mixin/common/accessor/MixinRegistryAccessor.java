package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Registry.class)
public interface MixinRegistryAccessor {
    @Accessor("WRITABLE_REGISTRY")
    static WritableRegistry<WritableRegistry<?>> getRoot() {
        throw new AssertionError("This shouldn't happen.");
    }
}
