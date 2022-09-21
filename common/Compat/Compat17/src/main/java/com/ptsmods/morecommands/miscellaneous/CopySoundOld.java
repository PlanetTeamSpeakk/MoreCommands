package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class CopySoundOld extends AbstractTickableSoundInstance {

    public CopySoundOld() {
        super(IMoreCommands.get().getCopySound().get(), SoundSource.MASTER);
        volume = .25f;
        looping = false;
        tick();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return true;
    }

    @Override
    public void tick() {
        Vec3 pos = Objects.requireNonNull(Minecraft.getInstance().player).position();
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }
}
