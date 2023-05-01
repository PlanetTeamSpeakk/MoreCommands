package com.ptsmods.morecommands.mixin.reach;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    @ModifyConstant(method = "handleUseItemOn", constant = @Constant(doubleValue = 64.0D))
    public double onPlayerInteractBlock_maxReach(double d) {
        return ReachCommand.getReach(player, true);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"), method = "handleUseItemOn", require = 0)
    public double onPlayerInteractBlock_squaredDistanceTo(Vec3 instance, Vec3 vec) {
        return instance.distanceToSqr(vec) < ReachCommand.getReach(player, true) ? 0 : 65;
    }
}
