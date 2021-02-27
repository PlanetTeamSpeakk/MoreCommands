package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Rainbow;
import com.ptsmods.morecommands.mixin.common.MixinItemAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Shadow private String splashText;
    private static boolean mc_isInitialised = false;

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Random; nextFloat()F", remap = false), method = "<init>(Z)V")
    private float init_nextFloat(Random random) {
        return ClientOptions.Tweaks.alwaysMinceraft ? 0f : random.nextFloat();
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo cbi) { // Couldn't get it to work with ModifyVariable for whatever reason.
        if (!mc_isInitialised) {
            mc_isInitialised = true;
            // Doing this after all initialisation is completed to make sure all other mods have registered their blocks and items and stuff.
            Registry.ITEM.forEach(item -> {
                if (item.getGroup() == null) ((MixinItemAccessor) item).setGroup(MoreCommands.unobtainableItems);
            });
        }
        if (splashText == null) splashText = MinecraftClient.getInstance().getSplashTextLoader().get();
        if (splashText != null) splashText = ClientOptions.Tweaks.rainbowSplash ? Rainbow.RAINBOW + splashText : splashText;
    }

}
