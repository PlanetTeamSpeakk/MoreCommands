package com.ptsmods.morecommands.api.addons;

import net.minecraft.network.chat.Component;

public interface GuiMessageAddon {

    void mc$setStringContent(String content);
    String mc$getStringContent();
    Component mc$getRichContent();
    String mc$getStrippedContent();
    int mc$getId();
}
