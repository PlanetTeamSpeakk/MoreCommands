package com.ptsmods.morecommands.mixin.reach.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
	@Shadow public ServerPlayerEntity player;

	@ModifyConstant(method = "onPlayerInteractBlock(Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;)V", constant = @Constant(doubleValue = 64.0D))
	public double onPlayerInteractBlock_maxReach(double d) {
		return ReachCommand.getReach(player, true);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"), method = "onPlayerInteractBlock", require = 0)
	public double onPlayerInteractBlock_squaredDistanceTo(Vec3d instance, Vec3d vec) {
		return instance.squaredDistanceTo(vec) < ReachCommand.getReach(player, true) ? 0 : 65;
	}

	@Group(name = "modifyReachDistance", min = 1, max = 1)
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3222;method_5858(Lnet/minecraft/class_1297;)D", remap = false, ordinal = 0),
			method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V")
	public double onPlayerInteractEntity_squaredDistanceTo(ServerPlayerEntity player, Entity entity) {
		return player.squaredDistanceTo(entity) < ReachCommand.getReach(player, true) ? 0 : 36;
	}

	@Group(name = "modifyReachDistance", min = 1, max = 1)
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", ordinal = 0),
			method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V")
	public double onPlayerInteractEntity_squaredDistanceTo(Entity entity, Vec3d vector) {
		return entity.squaredDistanceTo(vector) < ReachCommand.getReach(player, true) ? 0 : 36;
	}
}
