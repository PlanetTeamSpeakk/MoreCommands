package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.addons.PaintingAddon;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Painting.class)
public abstract class MixinPainting implements PaintingAddon {

    @Shadow public abstract Holder<PaintingVariant> getVariant();

    @Shadow public abstract void setVariant(Holder<PaintingVariant> holder);

    @Override
    public Object mc$getVariant() {
        return getVariant().value();
    }

    @Override
    public void mc$setVariant(Object variant) {
        setVariant(Holder.direct((PaintingVariant) variant));
    }
}
