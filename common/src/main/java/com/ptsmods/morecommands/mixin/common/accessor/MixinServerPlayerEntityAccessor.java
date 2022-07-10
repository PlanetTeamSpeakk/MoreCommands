package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayer.class)
public interface MixinServerPlayerEntityAccessor {
    @Accessor void setLastSentExp(int exp);
}
