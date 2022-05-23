package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.addons.ScreenAddon;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen implements ScreenAddon {
    @Shadow protected @Final List<Element> children;
    @Shadow protected @Final List<ClickableWidget> buttons;

    @Override
    public void mc$clear() {
        children.clear();
        buttons.clear();
    }

    @Override
    public List<ClickableWidget> mc$getButtons() {
        return buttons;
    }

    @Override
    public <T extends ClickableWidget> T mc$addButton(T button) {
        children.add(button);
        buttons.add(button);
        return button;
    }
}
