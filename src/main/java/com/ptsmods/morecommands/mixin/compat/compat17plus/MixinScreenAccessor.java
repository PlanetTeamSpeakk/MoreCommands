package com.ptsmods.morecommands.mixin.compat.compat17plus;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface MixinScreenAccessor {
    @Invoker("clearChildren")
    void callClear();

//    @Invoker("addDrawableChild")
//    <T extends Element & Drawable & Selectable> T callAddDrawableChild(T drawableElement);
    // This accessor does not get correctly remapped in the refmap.
    // It gets remapped to 'addDrawableChild(L/;)L/;', which prevents the mod from loading.
    // I've been fixing this manually since I made this accessor, but I have now decided to
    // use ASM Reflection instead.
    // Nvm, scratch that. ASM is bae

    @Accessor("drawables")
    List<Drawable> getDrawables();
}
