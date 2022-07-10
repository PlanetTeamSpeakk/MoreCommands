package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
public interface MixinAbstractBlockAccessor {

    @Accessor
    boolean isHasCollision();
}
