package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.entity.ai.brain.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Task.class)
public interface MixinTaskAccessor {

    @Accessor
    void setStatus(Task.Status status);

    @Accessor
    void setEndTime(long endTime);
}
