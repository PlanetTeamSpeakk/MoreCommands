package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F"), method = "<init>(Z)V")
    private float init_nextFloat(RandomSource random) {
        return ((BooleanClientOption) ClientOption.getOptions().get(ClientOptionCategory.TWEAKS).get("Always Minceraft")).getValue() ? 0f : random.nextFloat();
    }
}
