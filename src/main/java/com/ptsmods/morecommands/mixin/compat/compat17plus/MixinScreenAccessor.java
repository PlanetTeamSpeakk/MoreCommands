package com.ptsmods.morecommands.mixin.compat.compat17plus;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface MixinScreenAccessor {
    @Invoker("clearChildren")
    void callClear();

    @Invoker("addDrawableChild")
    <T extends Element & Drawable & Selectable> T callAddButton(T button);

    @Accessor("drawables")
    List<Drawable> getDrawables();
}
