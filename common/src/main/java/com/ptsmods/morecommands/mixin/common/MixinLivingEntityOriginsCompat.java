package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.elevated.SpeedCommand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class MixinLivingEntityOriginsCompat {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"), method = "travel")
    private void travel_updateVelocity(LivingEntity thiz, float speed, Vec3 movementInput) {
        // Applying swim speed
        thiz.moveRelative(speed * (thiz instanceof Player ? (float) thiz.getAttributeValue(SpeedCommand.SpeedType.getSwimSpeedAttribute()) : 1f), movementInput);
    }
}
