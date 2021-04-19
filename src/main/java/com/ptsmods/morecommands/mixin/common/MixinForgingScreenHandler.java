package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ForgingScreenHandler.class)
public class MixinForgingScreenHandler {
	@ModifyConstant(method = "method_24924(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Boolean;", constant = @Constant(doubleValue = 64.0D))
	public double method_24924_maxReach(double d, PlayerEntity player, World world, BlockPos pos) {
		return ReachCommand.getReach(player, true);
	}
}
