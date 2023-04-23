package com.ptsmods.morecommands.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.SignBlockEntity;

// Utility class so Compat subprojects may use certain mixins from the main project.
public interface MixinAccessWidener {

    @SuppressWarnings("deprecation")
    static MixinAccessWidener get() {
        return Holder.getMixinAccessWidener();
    }

    void serverPlayerEntity$setSyncedExperience(ServerPlayer player, int experience);

    char serverPlayNetworkHandler$gameMsgCharAt(ServerGamePacketListenerImpl thiz, String string, int index, ServerPlayer player, MinecraftServer server);

    Component[] signBlockEntity$getTexts(SignBlockEntity sbe);
}
