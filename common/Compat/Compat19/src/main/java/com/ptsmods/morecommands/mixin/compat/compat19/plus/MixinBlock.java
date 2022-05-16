package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class MixinBlock {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onStacksDropped(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;Z)V"),
			method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;" +
					"Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V")
	private static void dropStacks_onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean b, BlockState state0, World world0, BlockPos pos0,
												   @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack2) {
		// Don't drop XP if the spawner itself was dropped.
		if (state.getBlock() instanceof SpawnerBlock && !IMoreGameRules.get().checkBooleanWithPerm(world.getGameRules(), IMoreGameRules.get().doSilkSpawnersRule(), entity) || !(state.getBlock() instanceof SpawnerBlock))
			state.onStacksDropped(world, pos, stack, b);
	}
}
