package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.addons.PaintingEntityAddon;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Painting.class)
public class MixinPaintingEntity implements PaintingEntityAddon {

    @Shadow public Motive motive;

    @Override
    public Object mc$getVariant() {
        return motive;
    }

    @Override
    public void mc$setVariant(Object variant) {
        motive = (Motive) variant;
    }
}
