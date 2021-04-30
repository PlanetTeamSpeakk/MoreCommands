package com.ptsmods.morecommands.mixin.compat.compat17plus;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmlandBlock.class)
public class MixinFarmlandBlock {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V"), method = "onLandedUpon")
	private void onLandedUpon_setToDirt(FarmlandBlock farmlandBlock, World world, BlockState state, BlockPos pos, Entity entity, float f) {
		if (world.getGameRules().getBoolean(MoreCommands.doFarmlandTrampleRule)) FarmlandBlock.setToDirt(state, world, pos);
	}
}
