package com.ptsmods.morecommands.mixin.compat.compat16;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public class MixinServerPlayerEntity {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setLevel(Lnet/minecraft/world/level/Level;)V", remap = false), method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", remap = false)
    private void teleport_setWorld(ServerPlayer thiz, Level targetWorld, ServerLevel targetWorld0, double x, double y, double z, float yaw, float pitch) {
        thiz.moveTo(x, y, z, yaw, pitch);
    }
}
