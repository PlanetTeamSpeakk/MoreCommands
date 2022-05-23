package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;


@Mixin(CommandSuggestor.class)
public interface MixinCommandSuggestorAccessor {
    @Accessor
    CommandSuggestor.SuggestionWindow getWindow();

    @Accessor
    List<OrderedText> getMessages();

    @Accessor("x")
    int getX();

    @Accessor
    int getWidth();

}
