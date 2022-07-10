package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerPlayNetworkHandler {

    @Shadow public ServerPlayer player;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/resources/ResourceKey;)V"), method = "onDisconnect")
    public void onDisconnected_broadcastChatMessage(PlayerList playerManager, Component text, ResourceKey<ChatType> type) {
        if (IMoreGameRules.get().checkBooleanWithPerm(Objects.requireNonNull(playerManager.getServer().getLevel(Level.OVERWORLD)).getGameRules(), IMoreGameRules.get().doJoinMessageRule(), player)
                && !player.getEntityData().get(IDataTrackerHelper.get().vanish())) Compat.get().broadcast(playerManager, new Tuple<>(null, type.location()), text);
    }
}
