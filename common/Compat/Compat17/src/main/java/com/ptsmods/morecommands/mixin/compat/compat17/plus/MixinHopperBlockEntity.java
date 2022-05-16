package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {

	@Group(name = "insertAndExtract_setCooldown", min = 1, max = 1)
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity; setCooldown(I)V"), method = "insertAndExtract")
	private static void insertAndExtract_setCooldown(HopperBlockEntity hopper, int cooldown) {
		((MixinHopperBlockEntityAccessor) hopper).setTransferCooldown(Objects.requireNonNull(hopper.getWorld()).getGameRules().getInt(IMoreGameRules.get().hopperTransferCooldownRule()));
	}

	@Group(name = "insertAndExtract_setCooldown", min = 1, max = 1)
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;setTransferCooldown(I)V", remap = false), method = "insertAndExtract")
	private static void insertAndExtract_setCooldown_devEnv(HopperBlockEntity hopper, int cooldown) { // It has been remapped nowadays
		((MixinHopperBlockEntityAccessor) hopper).setTransferCooldown(Objects.requireNonNull(hopper.getWorld()).getGameRules().getInt(IMoreGameRules.get().hopperTransferCooldownRule()));
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory; removeStack(II)Lnet/minecraft/item/ItemStack;"), method = "insert")
	private static ItemStack insert_removeStack(Inventory inventory, int slot, int amount, World world, BlockPos blockPos, BlockState blockState, Inventory inventory0) {
		return inventory.removeStack(slot, world.getGameRules().getInt(IMoreGameRules.get().hopperTransferRateRule()));
	}
}
