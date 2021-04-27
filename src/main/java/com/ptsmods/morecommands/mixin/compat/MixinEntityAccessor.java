package com.ptsmods.morecommands.mixin.compat;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface MixinEntityAccessor {
    @Accessor("entityId")
    int getEntityId_();
}
