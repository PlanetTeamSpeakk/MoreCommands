package com.ptsmods.morecommands.api.util.compat;

import net.minecraft.server.level.ServerPlayer;

public interface ForgeCompat {
    boolean shouldRegisterListener();

    void registerListeners();

    void registerPermission(String permission, int defaultLevel, String desc);

    boolean checkPermission(ServerPlayer player, String permission);
}
