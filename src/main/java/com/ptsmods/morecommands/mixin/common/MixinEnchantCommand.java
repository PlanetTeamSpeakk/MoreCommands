package com.ptsmods.morecommands.mixin.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.EnchantCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(EnchantCommand.class)
public class MixinEnchantCommand {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"), method = "execute")
	private static int execute_getMaxLevel(Enchantment enchantment) {
		return Integer.MAX_VALUE;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"), method = "execute")
	private static boolean execute_isAcceptableItem(Enchantment enchantment, ItemStack stack) {
		return true;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;isCompatible(Ljava/util/Collection;Lnet/minecraft/enchantment/Enchantment;)Z"), method = "execute")
	private static boolean execute_isCompatible(Collection<Enchantment> enchantments, Enchantment enchantment) {
		return true;
	}
}
