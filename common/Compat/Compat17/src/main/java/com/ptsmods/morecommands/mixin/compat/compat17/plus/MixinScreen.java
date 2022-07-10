package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.addons.ScreenAddon;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
}
