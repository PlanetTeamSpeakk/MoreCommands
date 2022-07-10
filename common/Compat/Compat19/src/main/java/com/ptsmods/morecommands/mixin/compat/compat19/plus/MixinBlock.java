package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class MixinBlock {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"),
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V")
    private static void dropStacks_onStacksDropped(BlockState state, ServerLevel world, BlockPos pos, ItemStack stack, boolean b, BlockState state0, Level world0, BlockPos pos0,
                                                   @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack2) {
        // Don't drop XP if the spawner itself was dropped.
        if (state.getBlock() instanceof SpawnerBlock && !IMoreGameRules.get().checkBooleanWithPerm(world.getGameRules(), IMoreGameRules.get().doSilkSpawnersRule(), entity) || !(state.getBlock() instanceof SpawnerBlock))
            state.spawnAfterBreak(world, pos, stack, b);
    }
}
