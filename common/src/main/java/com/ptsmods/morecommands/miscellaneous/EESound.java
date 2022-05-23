package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

// This class is replaced with a dump in a normal environment.
public class EESound extends MovingSoundInstance {
    public EESound() {
        super(Objects.requireNonNull(Registry.SOUND_EVENT.get(new Identifier("morecommands:ee"))), SoundCategory.MASTER, Random.create());
        this.pitch = 0.0F;
        this.repeat = true;
        this.tick();
    }

    public boolean shouldAlwaysPlay() {
        return true;
    }

    public boolean canPlay() {
        return true;
    }

    public void tick() {
        Vec3d pos = MinecraftClient.getInstance().player.getPos();
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }
}
