package com.ptsmods.morecommands.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractContainerEventHandler implements ScreenAddon {

    @Override
    public void mc$clear() {
        clearWidgets();
    }

    @Shadow protected abstract void clearWidgets();

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;II)V", cancellable = true)
    public void renderTooltip(PoseStack matrices, Component text, int x, int y, CallbackInfo cbi) {
        TextBuilder<?> builder = Compat.get().builderFromText(text);
        if (builder instanceof TranslatableTextBuilder && "itemGroup.morecommands.unobtainable_items".equalsIgnoreCase(((TranslatableTextBuilder) builder).getKey())) {
            cbi.cancel();
            ReflectionHelper.<Screen>cast(this).renderComponentTooltip(matrices, Lists.newArrayList(text, LiteralTextBuilder.literal("MoreCommands", MoreCommands.DS)), x, y);
        }
    }
}
