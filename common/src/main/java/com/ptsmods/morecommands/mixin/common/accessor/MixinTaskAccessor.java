package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.entity.ai.behavior.Behavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Behavior.class)
public interface MixinTaskAccessor {

    @Accessor
    void setStatus(Behavior.Status status);

    @Accessor
    void setEndTimestamp(long endTimestamp);
}
