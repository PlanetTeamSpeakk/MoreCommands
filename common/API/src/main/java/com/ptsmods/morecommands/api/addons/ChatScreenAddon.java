package com.ptsmods.morecommands.api.addons;

import net.minecraft.client.gui.components.ChatComponent;

public interface ChatScreenAddon {
    GuiMessageAddon mc$getLine(ChatComponent hud, double x, double y);
}
