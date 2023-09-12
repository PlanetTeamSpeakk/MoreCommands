package com.ptsmods.morecommands.client.mixin.accessor;

import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;


@Mixin(CommandSuggestions.class)
public interface MixinCommandSuggestionsAccessor {
    @Accessor CommandSuggestions.SuggestionsList getSuggestions();
    @Accessor void setSuggestions(CommandSuggestions.SuggestionsList suggestions);

    @Accessor List<FormattedCharSequence> getCommandUsage();

    @Accessor int getCommandUsagePosition();

    @Accessor int getCommandUsageWidth();
}
