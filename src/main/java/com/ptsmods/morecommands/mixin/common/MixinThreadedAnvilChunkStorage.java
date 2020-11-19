package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.VanishCommand;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(ThreadedAnvilChunkStorage.class)
public class MixinThreadedAnvilChunkStorage {

    private static Method mc_stopTrackingMethod = null;
    private static Field mc_entryField = null;
    @Shadow @Final private Int2ObjectMap<?> entityTrackers;

    @Inject(at = @At("HEAD"), method = "unloadEntity(Lnet/minecraft/entity/Entity;)V", cancellable = true)
    public void unloadEntity(Entity entity, CallbackInfo cbi) {
        if (entity instanceof ServerPlayerEntity && entity.getDataTracker().get(MoreCommands.VANISH_TOGGLED)) {
            cbi.cancel();
            Object tracker = entityTrackers.remove(entity.getEntityId());
            if (tracker != null) {
                if (mc_stopTrackingMethod == null) mc_stopTrackingMethod = MoreCommands.getYarnMethod(tracker.getClass(), "stopTracking", "method_18733");
                if (mc_entryField == null) {
                    mc_entryField = MoreCommands.getYarnField(tracker.getClass(), "entry", "field_18246");
                    mc_entryField.setAccessible(true);
                }
                try {
                    mc_stopTrackingMethod.invoke(tracker);
                    VanishCommand.trackers.put((ServerPlayerEntity) entity, (EntityTrackerEntry) mc_entryField.get(tracker));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    MoreCommands.log.catching(e);
                }
            }
            entity.getDataTracker().set(MoreCommands.VANISH_TOGGLED, false);
        }
    }

}
