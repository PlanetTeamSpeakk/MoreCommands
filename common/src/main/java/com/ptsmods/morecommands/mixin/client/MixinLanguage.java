package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.miscellaneous.BetterLanguage;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Language.class)
public class MixinLanguage {
    @Shadow private static volatile Language instance;

    @Inject(at = @At("RETURN"), method = "create", cancellable = true)
    private static void create(CallbackInfoReturnable<Language> cbi) {
        cbi.setReturnValue(new BetterLanguage(cbi.getReturnValue()));
    }

    /**
     * @author PlanetTeamSpeak
     * @reason Make it better :)
     */
    @Overwrite
    public static void setInstance(Language language) {
        instance = new BetterLanguage(language);
    }
}
