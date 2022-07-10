package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface MixinLivingEntityAccessor {
    @Accessor
    void setDead(boolean dead);
}
