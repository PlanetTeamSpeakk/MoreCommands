package com.ptsmods.morecommands.mixin.compat.compat19plus;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.math.random.AbstractRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/AbstractRandom;nextFloat()F", remap = false), method = "<init>(Z)V")
    private float init_nextFloat(AbstractRandom random) {
        return ClientOptions.Tweaks.alwaysMinceraft.getValue() ? 0f : random.nextFloat();
    }
}
