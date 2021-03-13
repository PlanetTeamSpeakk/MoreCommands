package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface MixinSignBlockEntityAccessor {
    @Accessor
    void setEditable(boolean editable);
}
