package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.mixin.compat.MixinEntityAccessor;
import com.ptsmods.morecommands.mixin.compat.MixinScoreboardCriterionAccessor;
import com.ptsmods.morecommands.mixin.compat.MixinSimpleRegistryAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.Arrays;

public abstract class AbstractCompat extends CompatASMReflection implements Compat {
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

    @Override
    public void putCriterion(String name, ScoreboardCriterion criterion) {
        MixinScoreboardCriterionAccessor.getCriteria().put(name, criterion);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> boolean registryContainsId(SimpleRegistry<T> registry, Identifier id) {
        return ((MixinSimpleRegistryAccessor<T>) registry).getIdToEntry().containsKey(id);
    }

    @Override
    public PlayerListS2CPacket newPlayerListS2CPacket(int action, ServerPlayerEntity... players) {
        Class<?> actionClass = Arrays.stream(PlayerListS2CPacket.class.getClasses()).filter(c -> c.getEnumConstants() != null).findFirst().orElseThrow(() -> new IllegalStateException("Could not find Action inner class of PlayerListS2CPacket class."));
        return invokeCtor(getCtor(PlayerListS2CPacket.class, actionClass, ServerPlayerEntity[].class), actionClass.getEnumConstants()[action], players);
    }
}
