package com.ptsmods.morecommands.mixin.common;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(SharedSuggestionProvider.class)
public interface MixinCommandSource {

    /**
     * @author PlanetTeamSpeak
     * @reason I have to use an overwrite as ModifyArgs doesn't work in interfaces.
     * <p>
     * Makes identifier suggestions ignore underscores.
     * E.g. typing 'diamondsword' will suggest 'minecraft:diamond_sword'.
     */
    @Overwrite
    static <T> void filterResources(Iterable<T> candidates, String string, Function<T, ResourceLocation> toIdentifier, Consumer<T> action) {
        string = string.replace("_", "");
        boolean hasColon = string.indexOf(':') > -1;
        for (T t : candidates) {
            ResourceLocation identifier = toIdentifier.apply(t);
            if (hasColon && SharedSuggestionProvider.matchesSubStr(string, identifier.toString().replace("_", ""))) action.accept(t);
            else if (SharedSuggestionProvider.matchesSubStr(string, identifier.getNamespace()) ||
                    identifier.getNamespace().equals("minecraft") && SharedSuggestionProvider.matchesSubStr(string, identifier.getPath().replace("_", "")))
                action.accept(t);
        }
    }
}
