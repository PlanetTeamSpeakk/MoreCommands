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

import java.util.function.Supplier;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {

	@Shadow private int transferCooldown;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity; setCooldown(I)V"), method = "insertAndExtract(Ljava/util/function/Supplier;)Z")
	private void insertAndExtract_setCooldown(HopperBlockEntity thiz, int cooldown, Supplier<Boolean> extractMethod) {
		transferCooldown = thiz.getWorld().getGameRules().getInt(MoreCommands.hopperTransferCooldownRule);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity; removeStack(II)Lnet/minecraft/item/ItemStack;"), method = "insert()Z")
	private ItemStack insert_removeStack(HopperBlockEntity thiz, int slot, int amount) {
		return thiz.removeStack(slot, thiz.getWorld().getGameRules().getInt(MoreCommands.hopperTransferRateRule));
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory; removeStack(II)Lnet/minecraft/item/ItemStack;"), method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z")
	private static ItemStack extract_removeStack(Inventory inventory, int slot, int amount, Hopper hopper, Inventory inventory0, int slot0, Direction side) {
		return inventory.removeStack(slot, hopper.getWorld().getGameRules().getInt(MoreCommands.hopperTransferRateRule));
	}

}
