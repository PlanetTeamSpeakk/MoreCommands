package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.commands.server.elevated.VanishCommand;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityTrackerAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class MixinThreadedAnvilChunkStorage {
    @Shadow @Final private Int2ObjectMap<?> entityMap;

    @Inject(at = @At("HEAD"), method = "removeEntity", cancellable = true)
    public void unloadEntity(Entity entity, CallbackInfo cbi) {
        if (entity instanceof ServerPlayer && entity.getEntityData().get(IDataTrackerHelper.get().vanishToggled())) {
            cbi.cancel();
            Object tracker = entityMap.remove(entity.getId());
            if (tracker != null) {
                ((MixinEntityTrackerAccessor) tracker).callBroadcastRemoved();
                VanishCommand.trackers.put((ServerPlayer) entity, ((MixinEntityTrackerAccessor) tracker).getServerEntity());
            }
            entity.getEntityData().set(IDataTrackerHelper.get().vanishToggled(), false);
        }
    }
}
