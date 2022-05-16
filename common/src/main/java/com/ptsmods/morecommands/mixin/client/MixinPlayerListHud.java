package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {
	@Inject(at = @At("HEAD"), method = "renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V", cancellable = true)
	protected void renderLatencyIcon(MatrixStack matrixStack, int i, int j, int k, PlayerListEntry playerListEntry, CallbackInfo cbi) {
		if (ClientOptions.Rendering.showExactLatency.getValue()) {
			cbi.cancel();
			i -= 13;
			PlayerListHud thiz = ReflectionHelper.cast(this);
			thiz.setZOffset(thiz.getZOffset() + 100);
			int latency = playerListEntry.getLatency();
			float p = latency < 0 ? 100f : Math.min(100f / 900f * Math.max(latency-100, 0), 100f);
			if (p > 0) p = p / 100f;
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, "" + latency, j + i - 11, k, new Color((int) (p*255), (int) ((1f-p)*255), 0).getRGB());
			thiz.setZOffset(thiz.getZOffset() - 100);
		}
	}

	@ModifyVariable(at = @At("STORE"), index = 14, method = "render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")
	public int render_s(int s) {
		return s + (ClientOptions.Rendering.showExactLatency.getValue() ? 15 : 0);
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 3, method = "render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")
	public int render_fill_x2(int x2) {
		return x2 + (ClientOptions.Rendering.showExactLatency.getValue() ? 2 : 1);
	}
}
