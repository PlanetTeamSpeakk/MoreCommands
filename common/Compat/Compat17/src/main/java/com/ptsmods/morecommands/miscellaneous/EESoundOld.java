package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class EESoundOld extends AbstractTickableSoundInstance {

    public EESoundOld() {
        super(IMoreCommands.get().getEESound().get(), SoundSource.MASTER);
        pitch = 0f;
        looping = true;
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
