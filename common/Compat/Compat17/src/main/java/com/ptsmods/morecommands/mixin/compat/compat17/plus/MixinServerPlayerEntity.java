package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.EntityTeleportEvent;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
	@Inject(at = @At("HEAD"), method = "setWorld(Lnet/minecraft/server/world/ServerWorld;)V", cancellable = true)
	public void setWorld(ServerWorld world, CallbackInfo cbi) {
		Entity thiz = ReflectionHelper.cast(this);
		if (EntityTeleportEvent.EVENT.invoker().onTeleport(thiz, thiz.getEntityWorld(), world, thiz.getPos(), thiz.getPos())) cbi.cancel();
		if (thiz instanceof ServerPlayerEntity) MixinAccessWidener.get().serverPlayerEntity$setSyncedExperience((ServerPlayerEntity) thiz, -1); // Fix for the glitch that seemingly removes all your xp when you change worlds.
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity; setWorld(Lnet/minecraft/server/world/ServerWorld;)V"), method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V")
	private void teleport_setWorld(ServerPlayerEntity thiz, ServerWorld targetWorld, ServerWorld targetWorld0, double x, double y, double z, float yaw, float pitch) {
		thiz.refreshPositionAndAngles(x, y, z, yaw, pitch);
	}
}
