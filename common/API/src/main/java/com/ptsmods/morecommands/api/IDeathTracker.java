package com.ptsmods.morecommands.api;

import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public interface IDeathTracker {
    @SuppressWarnings("deprecation") // Holder not API
    static IDeathTracker get() {
        return Holder.getDeathTracker();
    }

    void addDeath(World world, Vec3d pos);

    void log(World world, Vec3d pos);

    List<Pair<Long, Vec3d>> getDeaths();

    void reset();

    default Pair<Long, Vec3d> getLastDeath() {
        List<Pair<Long, Vec3d>> deaths = getDeaths();
        return deaths.isEmpty() ? null : deaths.get(deaths.size() - 1);
    }
}
