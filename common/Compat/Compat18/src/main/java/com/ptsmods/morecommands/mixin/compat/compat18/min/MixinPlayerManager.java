package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.UUID;

@Mixin(PlayerList.class)
public class MixinPlayerManager {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"), method = "placeNewPlayer")
    public void onPlayerConnect_broadcastChatMessage(PlayerList thiz, Component msg, ChatType type, UUID id, Connection connection, ServerPlayer player) {
        if (IMoreGameRules.get().checkBooleanWithPerm(Objects.requireNonNull(thiz.getServer().getLevel(Level.OVERWORLD)).getGameRules(), IMoreGameRules.get().doJoinMessageRule(), player) &&
                !player.getEntityData().get(IDataTrackerHelper.get().vanish()))
            Compat.get().broadcast(thiz, new Tuple<>(type.ordinal(), null), msg);
    }
}
