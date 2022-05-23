package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.addons.ScreenAddon;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement implements ScreenAddon {

    @Shadow @Final private List<Selectable> selectables;
    @Shadow @Final private List<Element> children;
    @Shadow @Final private List<Drawable> drawables;

    @Override
    public void mc$clear() {
        clearChildren();
    }

    @Override
    public List<ClickableWidget> mc$getButtons() {
        return drawables.stream()
                .filter(drawable -> drawable instanceof ClickableWidget)
                .map(drawable -> (ClickableWidget) drawable)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends ClickableWidget> T mc$addButton(T button) {
        // Does exactly what #addDrawableChild(Element) does, but addDrawableChild cannot be remapped (likely because of its weird generic type).
        drawables.add(button);
        children.add(button);
        selectables.add(button);
        return button;
    }

    @Shadow protected abstract void clearChildren();
}
