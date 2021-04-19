package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {

	@Inject(at = @At("RETURN"), method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;")
	private static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cbi) {
		List<ItemStack> stacks = cbi.getReturnValue();
		if (blockEntity instanceof MobSpawnerBlockEntity && world.getGameRules().getBoolean(MoreCommands.doSilkSpawnersRule) && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			ItemStack spawner = new ItemStack(Items.SPAWNER);
			CompoundTag tag = blockEntity.toTag(new CompoundTag());
			tag.remove("x");
			tag.remove("y");
			tag.remove("z");
			spawner.getOrCreateTag().put("BlockEntityTag", tag);
			stacks.add(spawner);
		}
		return stacks;
	}

}
