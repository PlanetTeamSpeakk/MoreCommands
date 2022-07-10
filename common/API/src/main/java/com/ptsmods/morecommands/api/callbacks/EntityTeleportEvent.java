package com.ptsmods.morecommands.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface EntityTeleportEvent {
    Event<EntityTeleportEvent> EVENT = EventFactory.of(listeners -> (entity, worldFrom, worldTo, from, to) -> {
        for (EntityTeleportEvent listener : listeners) if (listener.onTeleport(entity, worldFrom, worldTo, from, to)) return true;
        return false;
    });

    boolean onTeleport(Entity entity, Level worldFrom, Level worldTo, Vec3 from, Vec3 to);
}
