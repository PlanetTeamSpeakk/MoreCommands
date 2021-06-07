package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mouse.class)
public interface MixinMouseAccessor {
    @Invoker
    void callOnMouseButton(long window, int button, int action, int mods);
}
