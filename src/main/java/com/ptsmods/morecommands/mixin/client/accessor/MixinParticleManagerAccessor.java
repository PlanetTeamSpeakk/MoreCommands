package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ParticleManager.class)
public interface MixinParticleManagerAccessor {

    @Accessor("PARTICLE_TEXTURE_SHEETS")
    static List<ParticleTextureSheet> getParticleTextureSheets() {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("PARTICLE_TEXTURE_SHEETS") @Mutable
    static void setParticleTextureSheets(List<ParticleTextureSheet> particleTextureSheets) {
        throw new AssertionError("This shouldn't happen.");
    }
}
