package com.ptsmods.morecommands.mixin.client;

import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParticleEngine.class)
public class MixinParticleManager {
//
//    @Shadow @Final @Mutable private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;
//
//    @Inject(at = @At("RETURN"), method = "<clinit>()V")
//    private static void clinit(CallbackInfo cbi) {
//        List<ParticleTextureSheet> list = new ArrayList<>(PARTICLE_TEXTURE_SHEETS);
//        list.add(VexParticle.pts);
//        PARTICLE_TEXTURE_SHEETS = list;
//    }

}
