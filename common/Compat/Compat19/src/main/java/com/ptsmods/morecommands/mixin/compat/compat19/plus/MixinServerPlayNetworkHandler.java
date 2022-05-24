package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

    @Shadow public ServerPlayerEntity player;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"), method = "onDisconnected(Lnet/minecraft/text/Text;)V")
    public void onDisconnected_broadcastChatMessage(PlayerManager playerManager, Text text, RegistryKey<MessageType> type) {
        if (IMoreGameRules.get().checkBooleanWithPerm(Objects.requireNonNull(playerManager.getServer().getWorld(World.OVERWORLD)).getGameRules(), IMoreGameRules.get().doJoinMessageRule(), player)
                && !player.getDataTracker().get(IDataTrackerHelper.get().vanish())) Compat.get().broadcast(playerManager, new Pair<>(null, type.getValue()), text);
    }
}
