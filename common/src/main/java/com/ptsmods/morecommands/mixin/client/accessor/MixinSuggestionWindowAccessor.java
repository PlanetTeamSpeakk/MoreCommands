package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CommandSuggestions.SuggestionsList.class)
public interface MixinSuggestionWindowAccessor {
    @Accessor
    Rect2i getRect();

    @Accessor
    @Mutable
    void setRect(Rect2i area);
}
