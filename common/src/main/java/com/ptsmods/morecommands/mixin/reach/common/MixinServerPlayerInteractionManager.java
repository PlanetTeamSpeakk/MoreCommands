package com.ptsmods.morecommands.mixin.reach.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
    @Shadow @Final protected ServerPlayerEntity player;

    @Group(name = "maxReach", min = 1, max = 1)
    @ModifyConstant(method = {"method_14263", "processBlockBreakingAction"}, remap = false, require = 0, constant = @Constant(doubleValue = 36.0D))
    public double processBlockBreakingAction_maxReach(double reach) {
        return ReachCommand.getReach(player, true);
    }

    @Group(name = "maxReach", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"), method = "processBlockBreakingAction", require = 0)
    public double processBlockBreakingAction_squaredDistanceTo(Vec3d instance, Vec3d vec) {
        return instance.squaredDistanceTo(vec) > ReachCommand.getReach(player, true) ? ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE + 1 : 0;
    }
}
