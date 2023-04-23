package com.ptsmods.morecommands.client.util;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeathTracker implements IDeathTracker {
    public static final DeathTracker INSTANCE = new DeathTracker();
    private final List<Tuple<Long, Tuple<ResourceLocation, Vec3>>> deaths = new ArrayList<>();

    @SneakyThrows
    private DeathTracker() {
        if (INSTANCE != null) throw new IllegalAccessException("There may only be one DeathTracker");
    }

    @Override
    public void addDeath(Level world, Vec3 pos) {
        deaths.add(new Tuple<>(System.currentTimeMillis(), new Tuple<>(world.dimension().location(), pos)));
        if (ClientOptions.Tweaks.trackDeaths.getValue()) log(world, pos);
    }

    @Override
    public void log(Level world, Vec3 pos) {
        Objects.requireNonNull(Minecraft.getInstance().player).sendSystemMessage(LiteralTextBuilder.literal(String.format(MoreCommands.DF + "You died at %s%s%s, %s%s%s, %s%s%s in dimension %s%s%s.",
                MoreCommands.SF, (int) pos.x(), MoreCommands.DF, MoreCommands.SF, (int) pos.y(), MoreCommands.DF, MoreCommands.SF, (int) pos.z(), MoreCommands.DF,
                MoreCommands.SF, world.dimensionTypeId().location(), MoreCommands.DF)));
    }

    @Override
    public void reset() {
        deaths.clear();
    }

    @Override
    public List<Tuple<Long, Tuple<ResourceLocation, Vec3>>> getDeaths() {
        return ImmutableList.copyOf(deaths);
    }
}
