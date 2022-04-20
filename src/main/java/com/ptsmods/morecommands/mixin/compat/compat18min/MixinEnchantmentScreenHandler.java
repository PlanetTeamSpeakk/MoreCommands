package com.ptsmods.morecommands.mixin.compat.compat18min;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EnchantmentScreenHandler.class)
public class MixinEnchantmentScreenHandler {
	@Unique private PlayerInventory playerInv = null;

	@Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V")
	private void init(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo cbi) {
		playerInv = playerInventory;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1890;method_8227(Ljava/util/Random;IILnet/minecraft/class_1799;)I", remap = false), method = "method_17411")
	private int onContentChanged_calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) return 0;
		else {
			if (bookshelfCount > 15 && MoreGameRules.checkBooleanWithPerm(playerInv.player.getWorld().getGameRules(), MoreGameRules.doEnchantLevelLimitRule, playerInv.player)) bookshelfCount = 15;
			int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
			if (slotIndex == 0) return Math.max(j / 3, 1);
			else return slotIndex == 1 ? j * 2 / 3 + 1 : Math.max(j, bookshelfCount * 2);
		}
	}
}
