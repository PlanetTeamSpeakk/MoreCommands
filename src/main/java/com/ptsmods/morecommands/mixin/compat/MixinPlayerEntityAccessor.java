package com.ptsmods.morecommands.mixin.compat;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface MixinPlayerEntityAccessor {
    @Accessor("inventory")
    PlayerInventory getInventory_();

    @Accessor("abilities")
    PlayerAbilities getAbilities_();
}
