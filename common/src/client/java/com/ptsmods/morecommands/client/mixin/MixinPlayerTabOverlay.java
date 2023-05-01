package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerTabOverlay.class)
public class MixinPlayerTabOverlay {
    @ModifyVariable(at = @At("STORE"), index = 14, method = "render")
    public int render_s(int s) {
        return s + (ClientOptions.Rendering.showExactLatency.getValue() ? 15 : 0);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"), index = 3, method = "render")
    public int render_fill_x2(int x2) {
        return x2 + (ClientOptions.Rendering.showExactLatency.getValue() ? 2 : 1);
    }
}
