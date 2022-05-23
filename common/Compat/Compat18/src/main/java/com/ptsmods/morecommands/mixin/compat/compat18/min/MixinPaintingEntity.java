package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.addons.PaintingEntityAddon;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PaintingEntity.class)
public class MixinPaintingEntity implements PaintingEntityAddon {

    @Shadow public PaintingMotive motive;

    @Override
    public Object mc$getVariant() {
        return motive;
    }

    @Override
    public void mc$setVariant(Object variant) {
        motive = (PaintingMotive) variant;
    }
}
