package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {

    // TODO
//    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;setCooldown(I)V"), method = "tryMoveItems")
//    private static void insertAndExtract_setCooldown(HopperBlockEntity hopper, int cooldown) {
//        ((MixinHopperBlockEntityAccessor) hopper).setCooldownTime(Objects.requireNonNull(hopper.getLevel()).getGameRules().getInt(IMoreGameRules.get().hopperTransferCooldownRule()));
//    }
//
//    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;removeItem(II)Lnet/minecraft/world/item/ItemStack;"), method = "ejectItems")
//    private static ItemStack insert_removeStack(Container inventory, int slot, int amount, Level world, BlockPos blockPos, BlockState blockState, Container inventory0) {
//        return inventory.removeItem(slot, world.getGameRules().getInt(IMoreGameRules.get().hopperTransferRateRule()));
//    }
}
