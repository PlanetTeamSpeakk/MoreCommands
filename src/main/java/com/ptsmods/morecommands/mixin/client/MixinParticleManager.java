package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.miscellaneous.VexParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
//
//	@Shadow @Final @Mutable private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;
//
//	@Inject(at = @At("RETURN"), method = "<clinit>()V")
//	private static void clinit(CallbackInfo cbi) {
//		List<ParticleTextureSheet> list = new ArrayList<>(PARTICLE_TEXTURE_SHEETS);
//		list.add(VexParticle.pts);
//		PARTICLE_TEXTURE_SHEETS = list;
//	}

}
