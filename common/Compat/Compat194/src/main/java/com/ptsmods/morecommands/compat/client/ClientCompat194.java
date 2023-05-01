package com.ptsmods.morecommands.compat.client;

import net.minecraft.client.gui.components.EditBox;

public class ClientCompat194 extends ClientCompat193 {
    @Override
    public void setFocused(EditBox editBox, boolean focused) {
        editBox.setFocused(focused);
    }
}
