package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface MixinSignBlockEntityAccessor {
    @Accessor
    Component[] getMessages();

    @Accessor
    void setIsEditable(boolean editable);
}
