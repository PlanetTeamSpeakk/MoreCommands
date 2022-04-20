package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.util.CompatHolder;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {

	@Inject(at = @At("RETURN"), method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;")
	private static void getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cbi) {
		List<ItemStack> stacks = cbi.getReturnValue();
		if (blockEntity instanceof MobSpawnerBlockEntity && MoreGameRules.checkBooleanWithPerm(world.getGameRules(), MoreGameRules.doSilkSpawnersRule, entity) && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			ItemStack spawner = new ItemStack(Items.SPAWNER);
			NbtCompound tag = CompatHolder.getCompat().writeBENBT(blockEntity);
			tag.remove("x");
			tag.remove("y");
			tag.remove("z");
			spawner.getOrCreateNbt().put("BlockEntityTag", tag);
			stacks.add(spawner);
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onStacksDropped(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"), method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V")
	private static void dropStacks_onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, BlockState state0, World world0, BlockPos pos0, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack2) {
		// Don't drop XP if the spawner itself was dropped.
		if (state.getBlock() instanceof SpawnerBlock && !MoreGameRules.checkBooleanWithPerm(world.getGameRules(), MoreGameRules.doSilkSpawnersRule, entity) || !(state.getBlock() instanceof SpawnerBlock))
			state.onStacksDropped(world, pos, stack);
	}
}
