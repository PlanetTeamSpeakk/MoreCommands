package com.ptsmods.morecommands.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IDeathTracker {
    @SuppressWarnings("deprecation") // Holder not API
    static IDeathTracker get() {
        return Holder.getDeathTracker();
    }

    void addDeath(Level world, Vec3 pos);

    void log(Level world, Vec3 pos);

    List<Tuple<Long, Tuple<ResourceLocation, Vec3>>> getDeaths();

    void reset();

    default Tuple<Long, Tuple<ResourceLocation, Vec3>> getLastDeath() {
        List<Tuple<Long, Tuple<ResourceLocation, Vec3>>> deaths = getDeaths();
        return deaths.isEmpty() ? null : deaths.get(deaths.size() - 1);
    }
}
