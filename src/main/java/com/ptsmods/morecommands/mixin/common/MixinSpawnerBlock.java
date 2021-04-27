package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlock.class)
public class MixinSpawnerBlock {

	@Inject(at = @At("HEAD"), method = "onStacksDropped(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V", cancellable = true)
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, CallbackInfo cbi) {
		if (world.getGameRules().getBoolean(MoreCommands.doSilkSpawnersRule) && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) cbi.cancel();
		// Don't drop XP if the spawner itself was dropped already.
	}

}
