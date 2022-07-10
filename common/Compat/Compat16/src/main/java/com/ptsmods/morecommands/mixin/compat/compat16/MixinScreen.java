package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.addons.ScreenAddon;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen implements ScreenAddon {
    @Shadow protected @Final List<GuiEventListener> children;
    @Shadow protected @Final List<AbstractWidget> buttons;

    @Override
    public void mc$clear() {
        children.clear();
        buttons.clear();
    }

    @Override
    public List<AbstractWidget> mc$getButtons() {
        return buttons;
    }

    @Override
    public <T extends AbstractWidget> T mc$addButton(T button) {
        children.add(button);
        buttons.add(button);
        return button;
    }
}
