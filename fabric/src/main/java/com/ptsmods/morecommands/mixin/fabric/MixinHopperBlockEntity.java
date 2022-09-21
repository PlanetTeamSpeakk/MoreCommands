package com.ptsmods.morecommands.mixin.fabric;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;removeItem(II)Lnet/minecraft/world/item/ItemStack;"), method = "ejectItems")
    private static ItemStack insert_removeStack(Container inventory, int slot, int amount, Level world, BlockPos blockPos, BlockState blockState, Container inventory0) {
        return inventory.removeItem(slot, world.getGameRules().getInt(IMoreGameRules.get().hopperTransferRateRule()));
    }
}
