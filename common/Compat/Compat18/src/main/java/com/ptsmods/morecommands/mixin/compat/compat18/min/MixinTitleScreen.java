package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Random; nextFloat()F", remap = false), method = "<init>(Z)V")
    private float init_nextFloat(Random random) {
        return ((BooleanClientOption) ClientOption.getOptions().get(ClientOptionCategory.TWEAKS).get("Always Minceraft")).getValue() ? 0f : random.nextFloat();
    }
}
