package com.ptsmods.morecommands.mixin.reach;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl17 {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"), method = "handleInteract")
    public double handleInteract_distanceToSqr(ServerPlayer player, Entity entity) {
        return player.distanceToSqr(entity) < IMoreCommands.get().getReach(player, true) ? 0 : 36;
    }

    @Redirect(method = "handleUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(DDD)D"))
    public double d(ServerPlayer player, double x, double y, double z) {
        return player.distanceToSqr(x, y, z) < IMoreCommands.get().getReach(player, true) ? 0 : 36;
    }
}
