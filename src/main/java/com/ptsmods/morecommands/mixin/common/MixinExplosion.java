package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class MixinExplosion {

	@Shadow @Final private World world;

	@Inject(at = @At("HEAD"), method = "collectBlocksAndDamageEntities()V", cancellable = true)
	public void collectBlocksAndDamageEntities(CallbackInfo cbi) {
		if (!world.getGameRules().getBoolean(MoreGameRules.doExplosionsRule)) cbi.cancel();
	}

	@Inject(at = @At("HEAD"), method = "affectWorld(Z)V", cancellable = true)
	public void affectWorld(boolean bl, CallbackInfo cbi) {
		if (!world.getGameRules().getBoolean(MoreGameRules.doExplosionsRule)) cbi.cancel();
	}

}
