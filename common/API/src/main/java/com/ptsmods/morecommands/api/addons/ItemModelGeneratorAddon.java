package com.ptsmods.morecommands.api.addons;

public interface ItemModelGeneratorAddon {

    boolean shouldIgnore();

    void ignoreNext();

    void resetIgnore();
}
