package com.ptsmods.morecommands.mixin.common;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerEntity.class)
public interface MixinServerPlayerEntityAccessor {
    @Accessor void setSyncedExperience(int i);
}
