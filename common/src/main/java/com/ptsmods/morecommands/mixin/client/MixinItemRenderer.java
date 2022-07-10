package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.commands.server.elevated.UnlimitedCommand;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Inject(at = @At("HEAD"), method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", cancellable = true)
    public void renderGuiItemOverlay(Font renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo cbi) {
        if (UnlimitedCommand.isUnlimited(stack) && (countLabel == null || !countLabel.endsWith("111"))) {
            if (countLabel != null) countLabel = countLabel.replace("" + stack.getCount(), "111");
            else countLabel = "111";
            cbi.cancel();
            ReflectionHelper.<ItemRenderer>cast(this).renderGuiItemDecorations(renderer, stack, x, y, countLabel);
        }
    }
}
