package com.ptsmods.morecommands.mixin.reach.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        if (entity instanceof Player) cbi.setReturnValue(ReachCommand.getReach((Player) entity, false));
    }

    @Inject(method = {"getSquaredReachDistance", "getSquaredAttackRange"}, at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void getSquaredDistance(LivingEntity entity, double value, CallbackInfoReturnable<Double> cbi) {
        if (entity instanceof Player) cbi.setReturnValue(ReachCommand.getReach((Player) entity, true));
    }
}
