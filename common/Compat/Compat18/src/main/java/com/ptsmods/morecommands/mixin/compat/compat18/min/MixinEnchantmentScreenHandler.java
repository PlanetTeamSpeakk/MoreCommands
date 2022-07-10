package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EnchantmentMenu.class)
public class MixinEnchantmentScreenHandler {
    @Unique private Inventory playerInv = null;

    @Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V")
    private void init(int syncId, Inventory playerInventory, ContainerLevelAccess context, CallbackInfo cbi) {
        playerInv = playerInventory;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Ljava/util/Random;IILnet/minecraft/world/item/ItemStack;)I"), method = "method_17411")
    private int onContentChanged_calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
        Item item = stack.getItem();
        int i = item.getEnchantmentValue();
        if (i <= 0) return 0;
        else {
            if (bookshelfCount > 15 && IMoreGameRules.get().checkBooleanWithPerm(playerInv.player.getLevel().getGameRules(), IMoreGameRules.get().doEnchantLevelLimitRule(), playerInv.player)) bookshelfCount = 15;
            int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
            if (slotIndex == 0) return Math.max(j / 3, 1);
            else return slotIndex == 1 ? j * 2 / 3 + 1 : Math.max(j, bookshelfCount * 2);
        }
    }
}
