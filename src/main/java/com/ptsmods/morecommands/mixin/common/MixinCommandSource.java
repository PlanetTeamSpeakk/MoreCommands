package com.ptsmods.morecommands.mixin.common;

import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(CommandSource.class)
public interface MixinCommandSource {

    /**
     * @author PlanetTeamSpeak
     * @reason I have to use an overwrite as ModifyArgs doesn't work in interfaces.
     *
     * Makes identifier suggestions ignore underscores.
     * E.g. typing 'diamondsword' will suggest 'minecraft:diamond_sword'.
     */
    @Overwrite
    static <T> void forEachMatching(Iterable<T> candidates, String string, Function<T, Identifier> toIdentifier, Consumer<T> action) {
        string = string.replace("_", "");
        boolean hasColon = string.indexOf(':') > -1;
        for (T t : candidates) {
            Identifier identifier = toIdentifier.apply(t);
            if (hasColon && CommandSource.shouldSuggest(string, identifier.toString().replace("_", ""))) action.accept(t);
            else if (CommandSource.shouldSuggest(string, identifier.getNamespace()) ||
                    identifier.getNamespace().equals("minecraft") && CommandSource.shouldSuggest(string, identifier.getPath().replace("_", "")))
                action.accept(t);
        }
    }
}
