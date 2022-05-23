package com.ptsmods.morecommands.mixin.reach.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes", remap = false, priority = 2000)
public class MixinReachEntityAttributes {
    @Inject(method = {"getReachDistance", "getAttackRange"}, at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void getDistance(LivingEntity entity, double value, CallbackInfoReturnable<Double> cbi) {
        if (entity instanceof PlayerEntity) cbi.setReturnValue(ReachCommand.getReach((PlayerEntity) entity, false));
    }

    @Inject(method = {"getSquaredReachDistance", "getSquaredAttackRange"}, at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void getSquaredDistance(LivingEntity entity, double value, CallbackInfoReturnable<Double> cbi) {
        if (entity instanceof PlayerEntity) cbi.setReturnValue(ReachCommand.getReach((PlayerEntity) entity, true));
    }
}
