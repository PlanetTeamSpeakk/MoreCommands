package com.ptsmods.morecommands.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.Rainbow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinInGameHud {

    @Inject(at = @At("HEAD"), method = "render")
    private void render(PoseStack stack, float tickDelta, CallbackInfo cbi) {
        if (ClientOptions.EasterEggs.rainbows.getValue() && Rainbow.getInstance() != null)
            GuiComponent.fill(stack, 0, 0, Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight(),
                    Rainbow.getInstance().getRainbowColour(false, 0.1f));
    }
}
