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
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractContainerEventHandler implements ScreenAddon {
    @Shadow @Final private List<NarratableEntry> narratables;
    @Shadow @Final private List<GuiEventListener> children;
    @Shadow @Final private List<Widget> renderables;

    @Override
    public void mc$clear() {
        clearWidgets();
    }

    @Override
    public List<AbstractWidget> mc$getButtons() {
        return renderables.stream()
                .filter(drawable -> drawable instanceof AbstractWidget)
                .map(drawable -> (AbstractWidget) drawable)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends AbstractWidget> T mc$addButton(T button) {
        // Does exactly what #addDrawableChild(Element) does, but addDrawableChild cannot be remapped (likely because of its weird generic type).
        renderables.add(button);
        children.add(button);
        narratables.add(button);
        return button;
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
