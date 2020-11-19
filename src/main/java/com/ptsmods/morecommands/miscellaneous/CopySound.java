package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class CopySound extends MovingSoundInstance {

    public CopySound() {
        super(Objects.requireNonNull(Registry.SOUND_EVENT.get(new Identifier("morecommands:copy"))), SoundCategory.MASTER);
        volume = .25f;
        repeat = false;
        tick();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public void tick() {
        Vec3d pos = MinecraftClient.getInstance().player.getPos();
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }

}
