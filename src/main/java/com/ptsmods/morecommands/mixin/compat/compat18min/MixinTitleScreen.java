package com.ptsmods.morecommands.mixin.compat.compat18min;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Random; nextFloat()F", remap = false), method = "<init>(Z)V")
    private float init_nextFloat(Random random) {
        return ClientOptions.Tweaks.alwaysMinceraft.getValue() ? 0f : random.nextFloat();
    }
}
