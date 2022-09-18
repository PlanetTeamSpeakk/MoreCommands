package com.ptsmods.morecommands.mixin.forge;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/server/network/ServerGamePacketListenerImpl$1")
public class MixinServerGamePacketListenerImpl$1 {

    // 1.18.2 and newer
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;canInteractWith(Lnet/minecraft/world/entity/Entity;D)Z"), method = "performInteraction")
    private boolean performInteract_canInteractWith(ServerPlayer player, Entity entity, double padding) {
        return player.distanceToSqr(entity) <= ReachCommand.getReach(player, true) || player.canInteractWith(entity, padding);
    }
}
