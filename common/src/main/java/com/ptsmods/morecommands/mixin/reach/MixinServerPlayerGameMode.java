package com.ptsmods.morecommands.mixin.reach;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {
    @Shadow @Final protected ServerPlayer player;

    @Group(name = "maxReach", min = 1, max = 1) // They added an int to the parameters in 1.19 and this mixin doesn't get loaded on Forge anyway.
    @ModifyConstant(method = "method_14263", constant = @Constant(doubleValue = 36.0D), remap = false)
    public double handleBlockBreakAction_maxDistance(double reach) {
        return ReachCommand.getReach(player, true);
    }

    @Group(name = "maxReach", min = 1, max = 1)
    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;MAX_INTERACTION_DISTANCE:D"), method = "handleBlockBreakAction")
    public double handleBlockBreakAction_maxDistance() {
        return ReachCommand.getReach(player, true);
    }
}
