package com.ptsmods.morecommands.api.addons;

import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;

public interface ScreenAddon {
    void mc$clear();

    List<AbstractWidget> mc$getButtons();

    <T extends AbstractWidget> T mc$addButton(T button);
}
