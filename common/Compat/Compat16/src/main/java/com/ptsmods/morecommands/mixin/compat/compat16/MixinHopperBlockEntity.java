package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
	@Shadow private int transferCooldown;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity; setCooldown(I)V", remap = false), method = "insertAndExtract", remap = false)
	private void insertAndExtract_setCooldown(HopperBlockEntity thiz, int cooldown, Supplier<Boolean> extractMethod) {
		transferCooldown = Objects.requireNonNull(thiz.getWorld()).getGameRules().getInt(IMoreGameRules.get().hopperTransferCooldownRule());
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;removeStack(II)Lnet/minecraft/item/ItemStack;", remap = false), method = "insert", remap = false)
	private ItemStack insert_removeStack(HopperBlockEntity thiz, int slot, int amount) {
		return thiz.removeStack(slot, Objects.requireNonNull(thiz.getWorld()).getGameRules().getInt(IMoreGameRules.get().hopperTransferRateRule()));
	}
}
