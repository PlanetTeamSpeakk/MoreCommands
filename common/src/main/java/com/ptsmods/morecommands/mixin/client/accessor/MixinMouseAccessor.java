package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MixinMouseAccessor {
    @Invoker
    void callOnPress(long window, int button, int action, int mods);
}
