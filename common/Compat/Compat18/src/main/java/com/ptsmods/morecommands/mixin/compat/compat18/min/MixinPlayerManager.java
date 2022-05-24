package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "onPlayerConnect")
    public void onPlayerConnect_broadcastChatMessage(PlayerManager thiz, Text msg, MessageType type, UUID id, ClientConnection connection, ServerPlayerEntity player) {
        if (IMoreGameRules.get().checkBooleanWithPerm(Objects.requireNonNull(thiz.getServer().getWorld(World.OVERWORLD)).getGameRules(), IMoreGameRules.get().doJoinMessageRule(), player) &&
                !player.getDataTracker().get(IDataTrackerHelper.get().vanish()))
            Compat.get().broadcast(thiz, new Pair<>(type.ordinal(), null), msg);
    }
}
