package com.ptsmods.morecommands.mixin.compat.compat17plus;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity; setCooldown(I)V"), method = "insertAndExtract(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/HopperBlockEntity;Ljava/util/function/BooleanSupplier;)Z")
    private static void insertAndExtract_setCooldown(HopperBlockEntity hopper, int cooldown) {
        ((MixinHopperBlockEntityAccessor) hopper).setTransferCooldown(Objects.requireNonNull(hopper.getWorld()).getGameRules().getInt(MoreCommands.hopperTransferCooldownRule));
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory; removeStack(II)Lnet/minecraft/item/ItemStack;"), method = "insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/inventory/Inventory;)Z")
    private static ItemStack insert_removeStack(Inventory inventory, int slot, int amount, World world, BlockPos blockPos, BlockState blockState, Inventory inventory0) {
        return inventory.removeStack(slot, world.getGameRules().getInt(MoreCommands.hopperTransferRateRule));
    }
}
