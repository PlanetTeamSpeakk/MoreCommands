package com.ptsmods.morecommands.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(PlayerTabOverlay.class)
public class MixinPlayerListHud {
    @Inject(at = @At("HEAD"), method = "renderPingIcon", cancellable = true)
    protected void renderLatencyIcon(PoseStack matrixStack, int i, int j, int k, PlayerInfo playerListEntry, CallbackInfo cbi) {
        if (ClientOptions.Rendering.showExactLatency.getValue()) {
            cbi.cancel();
            i -= 13;
            PlayerTabOverlay thiz = ReflectionHelper.cast(this);
            thiz.setBlitOffset(thiz.getBlitOffset() + 100);
            int latency = playerListEntry.getLatency();
            float p = latency < 0 ? 100f : Math.min(100f / 900f * Math.max(latency-100, 0), 100f);
            if (p > 0) p = p / 100f;
            Minecraft.getInstance().font.drawShadow(matrixStack, "" + latency, j + i - 11, k, new Color((int) (p*255), (int) ((1f-p)*255), 0).getRGB());
            thiz.setBlitOffset(thiz.getBlitOffset() - 100);
        }
    }

    @ModifyVariable(at = @At("STORE"), index = 14, method = "render")
    public int render_s(int s) {
        return s + (ClientOptions.Rendering.showExactLatency.getValue() ? 15 : 0);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"), index = 3, method = "render")
    public int render_fill_x2(int x2) {
        return x2 + (ClientOptions.Rendering.showExactLatency.getValue() ? 2 : 1);
    }
}
