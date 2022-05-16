package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Registry.class)
public interface MixinRegistryAccessor {
	@Accessor("ROOT")
	static MutableRegistry<MutableRegistry<?>> getRoot() {
		throw new AssertionError("This shouldn't happen.");
	}
}
