package com.ptsmods.morecommands.mixin.reach.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerInteractionManager {
    @Shadow @Final protected ServerPlayer player;

    @Group(name = "maxReach", min = 1, max = 1)
    @ModifyConstant(method = "handleBlockBreakAction", constant = @Constant(doubleValue = 36.0D))
    public double processBlockBreakingAction_maxReach(double reach) {
        return ReachCommand.getReach(player, true);
    }

    @Group(name = "maxReach", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"), method = "handleBlockBreakAction", require = 0)
    public double processBlockBreakingAction_squaredDistanceTo(Vec3 instance, Vec3 vec) {
        return instance.distanceToSqr(vec) > ReachCommand.getReach(player, true) ? ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE + 1 : 0;
    }
}
