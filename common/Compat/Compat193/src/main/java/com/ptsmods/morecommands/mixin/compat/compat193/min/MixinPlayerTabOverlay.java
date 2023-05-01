package com.ptsmods.morecommands.mixin.compat.compat193.min;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(PlayerTabOverlay.class)
public class MixinPlayerTabOverlay {

    @Inject(at = @At("HEAD"), method = "renderPingIcon", cancellable = true)
    protected void renderPingIcon(PoseStack matrixStack, int i, int j, int k, PlayerInfo playerListEntry, CallbackInfo cbi) {
        if (!ClientOption.getBoolean("showExactLatency")) return;

        cbi.cancel();
        i -= 13;
        PlayerTabOverlay thiz = ReflectionHelper.cast(this);
        thiz.setBlitOffset(thiz.getBlitOffset() + 100);
        int latency = playerListEntry.getLatency();
        float p = latency < 0 ? 100f : Math.min(100f / 900f * Math.max(latency-100, 0), 100f);
        if (p > 0) p = p / 100f;
        Minecraft.getInstance().font.drawShadow(matrixStack, String.valueOf(latency), j + i - 11, k,
                new Color((int) (p*255), (int) ((1f-p)*255), 0).getRGB());
        thiz.setBlitOffset(thiz.getBlitOffset() - 100);
    }
}
