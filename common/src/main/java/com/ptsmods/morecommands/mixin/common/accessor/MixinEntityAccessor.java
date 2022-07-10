package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface MixinEntityAccessor {

    @Accessor("yRot")
    float getYRot_();

    @Accessor("xRot")
    float getXRot_();

    @Accessor("id")
    int getId_();

    @Accessor("yRot")
    void setYRot_(float yRot);

    @Accessor("xRot")
    void setXRot_(float xRot);
}
