package com.ptsmods.morecommands.miscellaneous;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

// This class is replaced with a dump in a normal environment.
public class CopySound extends AbstractTickableSoundInstance {
    public CopySound() {
        super(Objects.requireNonNull(Registry.SOUND_EVENT.get(new ResourceLocation("morecommands:copy"))), SoundSource.MASTER, RandomSource.create());
        this.volume = 0.25F;
        this.looping = false;
        this.tick();
    }

    public boolean canStartSilent() {
        return true;
    }

    public boolean canPlaySound() {
        return true;
    }

    public void tick() {
        Vec3 pos = Minecraft.getInstance().player.position();
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }
}
