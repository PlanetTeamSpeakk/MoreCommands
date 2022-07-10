package com.ptsmods.morecommands.mixin.common;

import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(EnchantCommand.class)
public class MixinEnchantCommand {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"), method = "enchant")
    private static int execute_getMaxLevel(Enchantment enchantment) {
        return Integer.MAX_VALUE;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"), method = "enchant")
    private static boolean execute_isAcceptableItem(Enchantment enchantment, ItemStack stack) {
        return true;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;isEnchantmentCompatible(Ljava/util/Collection;Lnet/minecraft/world/item/enchantment/Enchantment;)Z"), method = "enchant")
    private static boolean execute_isCompatible(Collection<Enchantment> enchantments, Enchantment enchantment) {
        return true;
    }
}
