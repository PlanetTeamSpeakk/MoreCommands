package com.ptsmods.morecommands.mixin.addons;

import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.List;

public interface ScreenAddon {
    void mc$clear();
    List<ClickableWidget> mc$getButtons();
    <T extends ClickableWidget> T mc$addButton(T button);
}
