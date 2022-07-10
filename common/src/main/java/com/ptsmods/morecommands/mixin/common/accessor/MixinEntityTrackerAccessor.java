package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net/minecraft/server/level/ChunkMap$TrackedEntity")
public interface MixinEntityTrackerAccessor {
    @Invoker
    void callBroadcastRemoved();

    @Accessor
    ServerEntity getServerEntity();
}
