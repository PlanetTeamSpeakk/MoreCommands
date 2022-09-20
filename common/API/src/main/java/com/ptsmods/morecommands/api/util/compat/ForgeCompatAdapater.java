package com.ptsmods.morecommands.api.util.compat;

import net.minecraft.server.level.ServerPlayer;

public class ForgeCompatAdapater implements ForgeCompat {
    @Override
    public boolean shouldRegisterListener() {
        return false;
    }

    @Override
    public void registerListeners() {}

    @Override
    public void registerPermission(String permission, int defaultLevel, String desc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkPermission(ServerPlayer player, String permission) {
        throw new UnsupportedOperationException();
    }
}
