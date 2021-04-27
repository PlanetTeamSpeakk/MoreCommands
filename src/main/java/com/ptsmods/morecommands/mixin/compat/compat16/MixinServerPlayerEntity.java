package com.ptsmods.morecommands.mixin.compat.compat16;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3222; method_5866(Lnet/minecraft/class_1937;)V", remap = false), method = "method_14251(Lnet/minecraft/class_3218;DDDFF)V", remap = false)
    private void teleport_setWorld(ServerPlayerEntity thiz, World targetWorld, ServerWorld targetWorld0, double x, double y, double z, float yaw, float pitch) {
        thiz.refreshPositionAndAngles(x, y, z, yaw, pitch);
    }
}
