package com.ptsmods.morecommands.mixin.client;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.text.TextBuilder;
import com.ptsmods.morecommands.api.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.util.CompatHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
	@Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V", cancellable = true)
	public void renderTooltip(MatrixStack matrices, Text text, int x, int y, CallbackInfo cbi) {
		TextBuilder<?> builder = CompatHolder.getCompat().builderFromText(text);
		if (builder instanceof TranslatableTextBuilder && "itemGroup.morecommands.unobtainable_items".equalsIgnoreCase(((TranslatableTextBuilder) builder).getKey())) {
			cbi.cancel();
			ReflectionHelper.<Screen>cast(this).renderTooltip(matrices, Lists.newArrayList(text, LiteralTextBuilder.builder("MoreCommands", MoreCommands.DS).build()), x, y);
		}
	}
}
