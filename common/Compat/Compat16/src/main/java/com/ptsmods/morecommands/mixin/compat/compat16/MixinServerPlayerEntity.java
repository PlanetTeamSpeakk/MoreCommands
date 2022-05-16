package com.ptsmods.morecommands.mixin.compat.compat16;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setWorld(Lnet/minecraft/world/World;)V", remap = false), method = "teleport", remap = false)
	private void teleport_setWorld(ServerPlayerEntity thiz, World targetWorld, ServerWorld targetWorld0, double x, double y, double z, float yaw, float pitch) {
		thiz.refreshPositionAndAngles(x, y, z, yaw, pitch);
	}
}
