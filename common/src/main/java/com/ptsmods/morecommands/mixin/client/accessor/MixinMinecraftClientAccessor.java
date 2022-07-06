package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MixinMinecraftClientAccessor {
    @Accessor
    static int getCurrentFps() {
        throw new AssertionError("This shouldn't happen.");
    }
}
