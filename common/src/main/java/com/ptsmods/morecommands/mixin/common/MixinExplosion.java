package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class MixinExplosion {

    @Shadow @Final private Level level;

    @Inject(at = @At("HEAD"), method = "explode", cancellable = true)
    public void collectBlocksAndDamageEntities(CallbackInfo cbi) {
        if (!level.getGameRules().getBoolean(MoreGameRules.get().doExplosionsRule())) cbi.cancel();
    }

    @Inject(at = @At("HEAD"), method = "finalizeExplosion", cancellable = true)
    public void affectWorld(boolean bl, CallbackInfo cbi) {
        if (!level.getGameRules().getBoolean(MoreGameRules.get().doExplosionsRule())) cbi.cancel();
    }

}
