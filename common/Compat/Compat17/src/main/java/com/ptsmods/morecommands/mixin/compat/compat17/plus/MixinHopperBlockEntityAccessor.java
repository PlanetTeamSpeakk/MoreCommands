package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperBlockEntity.class)
public interface MixinHopperBlockEntityAccessor {
    @Accessor void setCooldownTime(int cooldown);
}
