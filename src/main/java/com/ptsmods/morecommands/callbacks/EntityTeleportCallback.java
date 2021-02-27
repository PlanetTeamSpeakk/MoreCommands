package com.ptsmods.morecommands.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface EntityTeleportCallback {
    Event<EntityTeleportCallback> EVENT = EventFactory.createArrayBacked(EntityTeleportCallback.class, callbacks -> (entity, worldFrom, worldTo, from, to) -> {
        for (EntityTeleportCallback callback : callbacks) if (callback.onTeleport(entity, worldFrom, worldTo, from, to)) return true;
        return false;
    });

    boolean onTeleport(Entity entity, World worldFrom, World worldTo, Vec3d from, Vec3d to);
}
