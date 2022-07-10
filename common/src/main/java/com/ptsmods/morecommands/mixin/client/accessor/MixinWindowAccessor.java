package com.ptsmods.morecommands.mixin.client.accessor;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Window.class)
public interface MixinWindowAccessor {
    @Invoker
    void callOnFramebufferResize(long window, int width, int height);
}
