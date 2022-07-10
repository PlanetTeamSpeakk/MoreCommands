package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
    @Shadow private int cooldownTime;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;setCooldown(I)V", remap = false), method = "tryMoveItems", remap = false)
    private void insertAndExtract_setCooldown(HopperBlockEntity thiz, int cooldown, Supplier<Boolean> extractMethod) {
        cooldownTime = Objects.requireNonNull(thiz.getLevel()).getGameRules().getInt(IMoreGameRules.get().hopperTransferCooldownRule());
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;removeItem(II)Lnet/minecraft/world/item/ItemStack;", remap = false), method = "ejectItems", remap = false)
    private ItemStack insert_removeStack(HopperBlockEntity thiz, int slot, int amount) {
        return thiz.removeItem(slot, Objects.requireNonNull(thiz.getLevel()).getGameRules().getInt(IMoreGameRules.get().hopperTransferRateRule()));
    }
}
