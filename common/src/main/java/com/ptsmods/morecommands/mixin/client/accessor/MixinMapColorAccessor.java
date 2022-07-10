package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MaterialColor.class)
public interface MixinMapColorAccessor {

    @Accessor("MATERIAL_COLORS")
    static MaterialColor[] getMaterialColors() {
        throw new AssertionError("This shouldn't happen!");
    }
}
