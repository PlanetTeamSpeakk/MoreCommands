package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net/minecraft/server/world/ThreadedAnvilChunkStorage$EntityTracker")
public interface MixinEntityTrackerAccessor {
    @Invoker
    void callStopTracking();

    @Accessor
    EntityTrackerEntry getEntry();
}
