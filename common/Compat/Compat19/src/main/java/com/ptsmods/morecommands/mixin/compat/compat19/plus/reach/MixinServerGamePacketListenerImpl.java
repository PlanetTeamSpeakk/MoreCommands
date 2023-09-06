package com.ptsmods.morecommands.mixin.compat.compat19.plus.reach;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    @Redirect(at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;MAX_INTERACTION_DISTANCE:D"),
            method = {"handleInteract", "handleUseItemOn"})
    public double onPlayerInteractEntity_maxInteractionDistance() {
        return IMoreCommands.get().getReach(player, true);
    }
}
