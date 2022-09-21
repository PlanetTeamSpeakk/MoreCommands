package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.mixin.common.accessor.MixinHopperBlockEntityAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;setCooldown(I)V"), method = "tryMoveItems")
    private static void insertAndExtract_setCooldown(HopperBlockEntity hopper, int cooldown) {
        ((MixinHopperBlockEntityAccessor) hopper).setCooldownTime(Objects.requireNonNull(hopper.getLevel()).getGameRules().getInt(IMoreGameRules.get().hopperTransferCooldownRule()));
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;removeItem(II)Lnet/minecraft/world/item/ItemStack;"), method = "tryTakeInItemFromSlot")
    private static ItemStack extract_removeStack(Container inventory, int slot, int amount, Hopper hopper, Container inventory0, int slot0, Direction side) {
        return inventory.removeItem(slot, Objects.requireNonNull(
                hopper instanceof BlockEntity ? ((BlockEntity) hopper).getLevel() :
                hopper instanceof Entity ? ((Entity) hopper).getCommandSenderWorld() :
                MoreCommands.serverInstance.getLevel(Level.OVERWORLD)).getGameRules().getInt(MoreGameRules.get().hopperTransferRateRule()));
    }
}
