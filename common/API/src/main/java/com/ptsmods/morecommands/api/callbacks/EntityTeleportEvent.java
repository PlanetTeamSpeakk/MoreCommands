package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface EntityTeleportEvent {
    Event<EntityTeleportEvent> EVENT = EventFactory.of(listeners -> (entity, worldFrom, worldTo, from, to) -> {
        for (EntityTeleportEvent listener : listeners) if (listener.onTeleport(entity, worldFrom, worldTo, from, to)) return true;
        return false;
    });

    boolean onTeleport(Entity entity, World worldFrom, World worldTo, Vec3d from, Vec3d to);
}
