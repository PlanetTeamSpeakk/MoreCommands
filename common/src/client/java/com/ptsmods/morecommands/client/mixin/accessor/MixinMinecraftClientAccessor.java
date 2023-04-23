package com.ptsmods.morecommands.client.mixin.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MixinMinecraftClientAccessor {
    @Accessor
    static int getFps() {
        throw new AssertionError("This shouldn't happen.");
    }
}
