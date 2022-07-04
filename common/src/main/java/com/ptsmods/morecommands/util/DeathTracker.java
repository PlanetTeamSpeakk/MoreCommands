package com.ptsmods.morecommands.util;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeathTracker implements IDeathTracker {
    public static final DeathTracker INSTANCE = new DeathTracker();
    private final List<Pair<Long, Vec3d>> deaths = new ArrayList<>();

    @SneakyThrows
    private DeathTracker() {
        if (INSTANCE != null) throw new IllegalAccessException("There may only be one DeathTracker");
    }

    @Override
    public void addDeath(World world, Vec3d pos) {
        deaths.add(new Pair<>(System.currentTimeMillis(), pos));
        if (ClientOptions.Tweaks.trackDeaths.getValue()) log(world, pos);
    }

    @Override
    public void log(World world, Vec3d pos) {
        Objects.requireNonNull(MinecraftClient.getInstance().player).sendMessage(LiteralTextBuilder.literal(String.format(MoreCommands.DF + "You died at %s%s%s, %s%s%s, %s%s%s in dimension %s%s%s.",
                MoreCommands.SF, (int) pos.getX(), MoreCommands.DF, MoreCommands.SF, (int) pos.getY(), MoreCommands.DF, MoreCommands.SF, (int) pos.getZ(), MoreCommands.DF,
                MoreCommands.SF, world.getDimensionKey().getValue(), MoreCommands.DF)));
    }

    @Override
    public void reset() {
        deaths.clear();
    }

    @Override
    public List<Pair<Long, Vec3d>> getDeaths() {
        return ImmutableList.copyOf(deaths);
    }
}
