package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory; removeStack(II)Lnet/minecraft/item/ItemStack;"), method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z")
	private static ItemStack extract_removeStack(Inventory inventory, int slot, int amount, Hopper hopper, Inventory inventory0, int slot0, Direction side) {
		return inventory.removeStack(slot, Objects.requireNonNull(((HopperBlockEntity) hopper).getWorld()).getGameRules().getInt(MoreCommands.hopperTransferRateRule));
	}
}
