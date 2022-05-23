package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.VanishCommand;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityTrackerAccessor;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public class MixinThreadedAnvilChunkStorage {
    @Shadow @Final private Int2ObjectMap<?> entityTrackers;

    @Inject(at = @At("HEAD"), method = "unloadEntity(Lnet/minecraft/entity/Entity;)V", cancellable = true)
    public void unloadEntity(Entity entity, CallbackInfo cbi) {
        if (entity instanceof ServerPlayerEntity && entity.getDataTracker().get(DataTrackerHelper.VANISH_TOGGLED)) {
            cbi.cancel();
            Object tracker = entityTrackers.remove(entity.getId());
            if (tracker != null) {
                ((MixinEntityTrackerAccessor) tracker).callStopTracking();
                VanishCommand.trackers.put((ServerPlayerEntity) entity, ((MixinEntityTrackerAccessor) tracker).getEntry());
            }
            entity.getDataTracker().set(DataTrackerHelper.VANISH_TOGGLED, false);
        }
    }
}
