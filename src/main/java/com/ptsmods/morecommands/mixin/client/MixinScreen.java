package com.ptsmods.morecommands.mixin.client;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V", cancellable = true)
    public void renderTooltip(MatrixStack matrices, Text text, int x, int y, CallbackInfo cbi) {
        if (text instanceof TranslatableText && "itemGroup.morecommands.unobtainable_items".equalsIgnoreCase(((TranslatableText) text).getKey())) {
            cbi.cancel();
            ReflectionHelper.<Screen>cast(this).renderTooltip(matrices, Lists.newArrayList(text, new LiteralText("MoreCommands").setStyle(MoreCommands.DS)), x, y);
        }
    }
}
