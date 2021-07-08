package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.commands.server.elevated.UnlimitedCommand;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
	@Inject(at = @At("HEAD"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", cancellable = true)
	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo cbi) {
		if (UnlimitedCommand.isUnlimited(stack) && (countLabel == null || !countLabel.endsWith("111"))) {
			if (countLabel != null) countLabel = countLabel.replace("" + stack.getCount(), "111");
			else countLabel = "111";
			cbi.cancel();
			ReflectionHelper.<ItemRenderer>cast(this).renderGuiItemOverlay(renderer, stack, x, y, countLabel);
		}
	}
}
