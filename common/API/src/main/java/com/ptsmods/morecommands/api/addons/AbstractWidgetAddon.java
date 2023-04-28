package com.ptsmods.morecommands.api.addons;

public interface AbstractWidgetAddon {
    void setValidButtons(int... buttons);

    int getLastMouseButton();
}
