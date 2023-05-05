package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.miscellaneous.BetterLanguage;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Language.class)
public class MixinLanguage {
    @Shadow private static volatile Language instance;

    @Inject(at = @At("RETURN"), method = "loadDefault", cancellable = true)
    private static void create(CallbackInfoReturnable<Language> cbi) {
        cbi.setReturnValue(new BetterLanguage(cbi.getReturnValue()));
    }

    /**
     * @author PlanetTeamSpeak
     * @reason Make it better :)
     */
    @Overwrite
    public static void inject(Language language) {
        instance = new BetterLanguage(language);
    }
}