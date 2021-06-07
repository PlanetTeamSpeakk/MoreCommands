package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.util.math.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CommandSuggestor.SuggestionWindow.class)
public interface MixinSuggestionWindowAccessor {
    @Accessor
    Rect2i getArea();

    @Accessor
    void setArea(Rect2i area);
}
