package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.SpeedCommand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class MixinLivingEntityOriginsCompat {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity; updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"), method = "travel(Lnet/minecraft/util/math/Vec3d;)V")
    private void travel_updateVelocity(LivingEntity thiz, float speed, Vec3d movementInput) {
        // Applying swim speed
        thiz.updateVelocity(speed * (thiz instanceof PlayerEntity ? (float) thiz.getAttributeValue(SpeedCommand.SpeedType.swimSpeedAttribute) : 1f), movementInput);
    }
}
