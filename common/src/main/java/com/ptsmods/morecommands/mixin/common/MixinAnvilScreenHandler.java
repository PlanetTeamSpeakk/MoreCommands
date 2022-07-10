package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class MixinAnvilScreenHandler {
    @Unique private Inventory playerInv;

    @Inject(at = @At("TAIL"), method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V")
    public void init(int syncId, Inventory inventory, ContainerLevelAccess context, CallbackInfo cbi) {
        playerInv = inventory;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getBaseRepairCost()I"), method = "createResult")
    public int updateResult_getRepairCost(ItemStack stack) {
        return MoreGameRules.get().checkBooleanWithPerm(playerInv.player.level.getGameRules(), MoreGameRules.get().doPriorWorkPenaltyRule(), playerInv.player) ? stack.getBaseRepairCost() : 0;
    }
}
