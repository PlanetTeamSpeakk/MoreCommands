package com.ptsmods.morecommands.mixin.common;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ptsmods.morecommands.api.addons.EntitySelectorAddon;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(EntitySelectorParser.class)
public abstract class MixinEntitySelectorReader {
    @Shadow @Final private StringReader reader;
    @Shadow private int maxResults;
    @Shadow private boolean includesEntities;
    @Shadow private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;
    private @Unique boolean targetOnly = false;

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;setCursor(I)V"), method = "parseSelector", locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void customSelectors(CallbackInfo cbi, int cursor, char ch) throws CommandSyntaxException {
        boolean isCustomSelector = false;
        if (ch == 't') {
            cbi.cancel();
            maxResults = 1;
            includesEntities = true;
            targetOnly = true;
            isCustomSelector = true;
        }

        if (!isCustomSelector) return;

        suggestions = this::suggestOpenOptions;
        if (reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
        }
    }

    @Inject(at = @At("RETURN"), method = "getSelector")
    private void build(CallbackInfoReturnable<EntitySelector> cbi) {
        ((EntitySelectorAddon) cbi.getReturnValue()).setTargetOnly(targetOnly);
    }

    @Inject(at = @At("TAIL"), method = "fillSelectorSuggestions")
    private static void suggestCustomSelectors(SuggestionsBuilder builder, CallbackInfo cbi) {
        builder.suggest("@t");
    }

    @Shadow protected abstract CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

    @Shadow protected abstract CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer);

    @Shadow protected abstract void parseOptions() throws CommandSyntaxException;
}
