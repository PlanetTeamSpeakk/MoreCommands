package com.ptsmods.morecommands.mixin.compat.compat190;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(PlayerList.class)
public class MixinPlayerList {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/resources/ResourceKey;)V"),
            method = "placeNewPlayer")
    public void onPlayerConnect_broadcastChatMessage(PlayerList playerManager, Component text, ResourceKey<ChatType> registryKey, Connection connection, ServerPlayer player) {
        if (IMoreGameRules.get().checkBooleanWithPerm(Objects.requireNonNull(playerManager.getServer().getLevel(Level.OVERWORLD)).getGameRules(), IMoreGameRules.get().doJoinMessageRule(), player) &&
                !player.getEntityData().get(IDataTrackerHelper.get().vanish()))
            playerManager.broadcastSystemMessage(text, registryKey);
    }
}
