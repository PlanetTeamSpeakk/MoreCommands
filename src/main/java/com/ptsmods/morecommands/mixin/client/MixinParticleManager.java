package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.VexParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Shadow @Final private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

    @Inject(at = @At("RETURN"), method = "<clinit>()V")
    private static void clinit(CallbackInfo cbi) {
        Field f = MoreCommands.getYarnField(ParticleManager.class, "PARTICLE_TEXTURE_SHEETS", "field_17820");
        f.setAccessible(true);
        MoreCommands.removeFinalModifier(f);
        try {
            List<ParticleTextureSheet> list = new ArrayList<>((List<ParticleTextureSheet>) f.get(null));
            list.add(VexParticle.pts);
            f.set(null, list);
        } catch (IllegalAccessException e) {
            MoreCommandsClient.log.catching(e);
        }
    }

}
