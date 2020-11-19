package com.ptsmods.morecommands.mixin.client;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Window.class)
public interface MixinWindow {

    @Invoker
    void callOnFramebufferSizeChanged(long window, int width, int height);

}
