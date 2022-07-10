package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(at = @At("RETURN"), method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;")
    private static void getDroppedStacks(BlockState state, ServerLevel world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cbi) {
        List<ItemStack> stacks = cbi.getReturnValue();
        if (blockEntity instanceof SpawnerBlockEntity && MoreGameRules.get().checkBooleanWithPerm(world.getGameRules(), MoreGameRules.get().doSilkSpawnersRule(), entity) &&
                EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
            ItemStack spawner = new ItemStack(Items.SPAWNER);
            CompoundTag tag = Compat.get().writeBENBT(blockEntity);
            tag.remove("x");
            tag.remove("y");
            tag.remove("z");
            spawner.getOrCreateTag().put("BlockEntityTag", tag);
            stacks.add(spawner);
        }
    }
}
