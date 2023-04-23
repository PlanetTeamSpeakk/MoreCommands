package com.ptsmods.morecommands.mixin.forge;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    // 1.18 and older
    @ModifyConstant(method = "handleInteract", constant = @Constant(doubleValue = 36.0))
    private double handleInteract_maxReach(double reach) {
        return ReachCommand.getReach(player, true);
    }
}
