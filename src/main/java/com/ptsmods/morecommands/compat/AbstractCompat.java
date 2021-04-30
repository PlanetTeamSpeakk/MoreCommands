package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.mixin.compat.MixinEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractCompat implements Compat {
    @Override
    public char gameMsgCharAt(ServerPlayNetworkHandler thiz, String string, int index, ServerPlayerEntity player, MinecraftServer server) {
        char ch = string.charAt(index);
        if (!string.startsWith("/") && ch == '\u00A7' && (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doChatColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel())))
            ch = '&';
        return ch;
    }

    @Override
    public int getEntityId(Entity entity) {
        return ((MixinEntityAccessor) entity).getEntityId_();
    }

    @Override
    public float getEntityYaw(Entity entity) {
        return ((MixinEntityAccessor) entity).getYaw_();
    }

    @Override
    public float getEntityPitch(Entity entity) {
        return ((MixinEntityAccessor) entity).getPitch_();
    }

    @Override
    public void setEntityYaw(Entity entity, float yaw) {
        ((MixinEntityAccessor) entity).setYaw_(yaw);
    }

    @Override
    public void setEntityPitch(Entity entity, float pitch) {
        ((MixinEntityAccessor) entity).setPitch_(pitch);
    }
}
