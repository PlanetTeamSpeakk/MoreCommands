package com.ptsmods.morecommands.mixin.reach.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

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

    @Group(name = "modifyReachDistance", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3222;method_5858(Lnet/minecraft/class_1297;)D", remap = false, ordinal = 0),
            method = "handleInteract")
    public double onPlayerInteractEntity_squaredDistanceTo(ServerPlayer player, Entity entity) {
        return player.distanceToSqr(entity) < ReachCommand.getReach(player, true) ? 0 : 36;
    }

    @Group(name = "modifyReachDistance", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_20280_(Lnet/minecraft/world/entity/Entity;)D", remap = false, ordinal = 0),
            method = "handleInteract")
    public double onPlayerInteractEntity_squaredDistanceToMoj(ServerPlayer player, Entity entity) {
        return player.distanceToSqr(entity) < ReachCommand.getReach(player, true) ? 0 : 36;
    }

    @Group(name = "modifyReachDistance", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D", ordinal = 0),
            method = "handleInteract")
    public double onPlayerInteractEntity_squaredDistanceTo(Entity entity, Vec3 vector) {
        return entity.distanceToSqr(vector) < ReachCommand.getReach(player, true) ? 0 : 36;
    }
}
