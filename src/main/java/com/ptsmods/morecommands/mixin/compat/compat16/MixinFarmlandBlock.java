package com.ptsmods.morecommands.mixin.compat.compat16;

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
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2344;method_10125(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)V", remap = false), method = "method_9554", remap = false)
	private void onLandedUpon_setToDirt(BlockState state, World world, BlockPos pos, World world0, BlockPos pos0, Entity entity, float distance) {
		if (world.getGameRules().getBoolean(MoreCommands.doFarmlandTrampleRule)) FarmlandBlock.setToDirt(state, world, pos);
	}
}
